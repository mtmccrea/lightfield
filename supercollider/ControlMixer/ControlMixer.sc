ControlMixer {
	// copyArga
	var <broadcastTag, <broadcastAddr, <broadcastRate, <server, loadCond, colorShift;

	var <busnum, ratePeriodSpec, <oscTag, <ctlFades, <ctlViews, outVal;
	var <mixView, msgTxt, broadcastChk, plotChk, updateBx, outValTxt;
	var nBoxWidth = 30, validLFOs, <plotter, <ctlLayout, plotterAdded = false;
	var broadcastBus, broadcastWaittime, broadcastTag, pollTask, broadcasting=false;
	var baseColor, idColor, mixColor, colorStep;

	*new { | broadcastTag="/myMessage", broadcastNetAddr, broadcastRate=15, server, loadCond, colorShift=0.03 |
		^super.newCopyArgs( broadcastTag, broadcastNetAddr, broadcastRate, server, loadCond, colorShift ).init;
	}

	init {

		broadcastWaittime = broadcastRate.reciprocal;
		broadcastAddr = broadcastAddr ?? {NetAddr("localhost", 57120)};
		(broadcastTag.asString[0].asSymbol != '/').if{ broadcastTag = "/" ++ broadcastTag };

		ctlFades = [];
		ctlViews = [];
		server = server ?? Server.default;
		this.prDefineColors;
		server.waitForBoot({
			busnum = server.controlBusAllocator.alloc(1);
			postf("Creating ControlMixer to output to %\n", busnum);

			// create a ctk bus to read from for broadcasting, with the same busnum that the ControlFades write to
			broadcastBus = CtkControl(1, 0, 0, busnum);

			validLFOs = [
				'static', SinOsc, LFPar, LFTri, LFCub, LFDNoise0, LFDNoise1, LFDNoise3
			].collect(_.asSymbol);

			ratePeriodSpec = ControlSpec(15.reciprocal, 15, 2.5, default: 3);

			pollTask = Task({
				inf.do{
					broadcastBus.get({|busnum, val|
						outVal = val;
						defer{ outValTxt.string_(val.round(0.001)) };
					});

					broadcasting.if{
						broadcastAddr.sendMsg(broadcastTag, outVal)
					};

					broadcastWaittime.wait
				}
			});

			pollTask.play;

			this.makeView;

			this.addCtl;

			loadCond !? {loadCond.test_(true).signal};
		});
	}

	addCtl { |min= -1, max=1, finishCond|

		var ctl, view, sclSpec, offsSpec, updateOffset;
		var minBx, maxBx, rateBx, rateSl, rateTxt, periodChk, mixBx;
		var valBx, mixKnb, sigPUp, rmvBut, sclBx, sclKnb, offsBx, offsKnb, completeFunc;

		sclSpec = ControlSpec(0, 2, 'lin', default: 1);
		// offsSpec = ControlSpec(max.neg, max, 'lin', default: 0);
		offsSpec = ControlSpec(-1, 1, 'lin', default: 0);

		completeFunc = finishCond.notNil.if({ {finishCond.test_(true).signal} },{ {} });

		ctl = ControlFade(fadeTime: 0.0, initVal: 0, busnum: busnum, server: server, onComplete: completeFunc);

		ctlFades = ctlFades.add(ctl);

		view = View().background_(mixColor).maxHeight_(125)
		.layout_(
			VLayout(

				HLayout(
					[ VLayout(
						StaticText().string_("min"),
						minBx = NumberBox().fixedWidth_(nBoxWidth)
					).spacing_(0), a: \left ],
					[ VLayout(
						StaticText().string_("max"),
						maxBx = NumberBox().fixedWidth_(nBoxWidth)
					).spacing_(0), a: \left ],
					[ VLayout(
						StaticText().string_("Signal"),
						sigPUp = PopUpMenu().maxWidth_(125)
					).spacing_(0), a: \left ],
					[ VLayout(
						StaticText().string_("period").align_(\center),
						periodChk = CheckBox().fixedWidth_(15),
					).spacing_(0), a: \left ],

					nil,
					[ VLayout(
						StaticText().string_("StaticVal").align_(\left),
						valBx = NumberBox().fixedWidth_(nBoxWidth*1.2)
					).spacing_(5), a: \right ],
					nil,
					[ rmvBut = Button().states_([["X", Color.black, Color.red]]).fixedWidth_(nBoxWidth/2).fixedHeight_(nBoxWidth/2), a: \topRight]
				),
				HLayout(
					VLayout(
						HLayout(
							[ VLayout(
								rateTxt = StaticText().string_("Rate(sec)"),
								rateBx = NumberBox().fixedWidth_(nBoxWidth*1.5),
							).spacing_(0), a: \left ],
							rateSl = Slider().orientation_(\horizontal).maxHeight_(25).minWidth_(120),
						),
						HLayout(
							VLayout(
								StaticText().string_("scale").align_(\center),
								sclBx = NumberBox().fixedWidth_(nBoxWidth),
							).spacing_(0),
							sclKnb = Knob().mode_(\vert).centered_(true),
							VLayout(
								StaticText().string_("offset").align_(\center),
								offsBx = NumberBox().fixedWidth_(nBoxWidth),
							).spacing_(0),
							offsKnb = Knob().mode_(\vert).centered_(true),
							nil,
							VLayout(
								StaticText().string_("mix").align_(\right).fixedWidth_(nBoxWidth),
								mixBx = NumberBox().fixedWidth_(nBoxWidth).maxWidth_(50),
							).spacing_(0),
							mixKnb = Knob().mode_(\vert),
						)
					),
				)
			).margins_(4).spacing_(2)
		);

		ctlLayout.add( view );
		ctlViews = ctlViews.add(view);

		// TODO: move this out, make ctl arg or make a class for each control
		updateOffset = {
			var range;
			range = ctl.high - ctl.low;
			offsSpec.minval_(range.half.neg);
			offsSpec.maxval_(range.half);
		};

		// define the actions

		minBx.action_({ |bx|
			ctl.low_(bx.value);
			max = bx.value;
			this.updatePlotterBounds;
			// updateOffset.();
		}).value_(min);

		maxBx.action_({ |bx|
			ctl.high_(bx.value);
			max = bx.value;
			this.updatePlotterBounds;
			// updateOffset.();
		}).value_(max);

		sigPUp.items_(validLFOs).action_({|sl|
			if( sl.item.asSymbol != 'static' )
			{
				var rateHz;
				rateHz = ratePeriodSpec.map(rateSl.value).reciprocal;
				ctl.lfo_( sl.item.asSymbol, rateHz, minBx.value, maxBx.value)

			}
			{ ctl.value_( valBx.value ) };
		});

		valBx.action_({ |bx| ctl.value_(bx.value); sigPUp.value_(0)});

		rateSl.action_({ |sl|
			var rateSec, rateHz;

			rateSec = ratePeriodSpec.map(sl.value);
			rateHz = ratePeriodSpec.map(sl.value).reciprocal;

			ctl.freq_( rateHz );
			rateBx.value_( periodChk.value.asBoolean.if({rateSec},{rateHz}) );
		}).value_(ratePeriodSpec.unmap(ratePeriodSpec.default));

		rateBx.action_({ |bx|
			var rateSec, rateHz;

			if( periodChk.value.asBoolean,
				{	rateHz = bx.value.reciprocal;
					rateSec = bx.value;
				},
				{	rateHz = bx.value;
					rateSec = bx.value.reciprocal;
				}
			);
			ctl.freq_( rateHz );

			rateSl.value_( ratePeriodSpec.unmap(rateSec) );
		}).value_(ratePeriodSpec.default).clipLo_(0.0);

		periodChk.action_({ |chk|
			var bool, curRateBx;
			bool = chk.value.asBoolean;
			curRateBx = rateBx.value;
			rateBx.value_(curRateBx.reciprocal);
			bool.if({ rateTxt.string_("Rate(sec)") },{ rateTxt.string_("Rate(Hz)") });
		}).value_(true);

		offsBx.action_({|bx|
			ctl.offset_(bx.value);
			offsKnb.value_(offsSpec.unmap(bx.value));
		}).value_(offsSpec.default);

		offsKnb.action_({|knb|
			var val = offsSpec.map(knb.value);
			ctl.offset_(val);
			offsBx.value_(val);
		}).value_(offsSpec.unmap(offsSpec.default));

		sclBx.action_({|bx|
			ctl.scale_(bx.value);
			sclKnb.value_(sclSpec.unmap(bx.value));

		}).value_(sclSpec.default);

		sclKnb.action_({|knb| var val = sclSpec.map(knb.value);
			ctl.scale_(val);
			sclBx.value_(val);
		}).value_(sclSpec.unmap(sclSpec.default));


		plotterAdded.not.if{ this.addPlotter };

		rmvBut.action_({
			var vHeight = view.bounds.height;
			ctl.release(0.3, freeBus: false); // leave the bus running if others are writing to it

			block{ |break| ctlFades.do{ |cFade, i|
				if(cFade === ctl){
					ctlFades.removeAt(i); "removing a ctl".postln;
					ctlViews.removeAt(i); "removed ctl view".postln;
					break.()
				}
			}};

			fork({
				view.remove;
				0.1.wait;
				// win.setInnerExtent(win.view.bounds.width, win.view.bounds.height - vHeight );
			}, AppClock);
		});

		mixBx.action_({|bx| ctl.amp_(bx.value); mixKnb.value_(bx.value) }).value_(1);
		mixKnb.action_({|knb|
			var val = knb.value.sqrt;  // power scaling
			ctl.amp_(val); mixBx.value_(val)
		}).value_(1);

	}

	makeView {
		mixView = View().layout_(
			VLayout(
					ctlLayout = VLayout(
						View().background_(idColor).layout_(
							HLayout(
								[ msgTxt = TextField().string_(broadcastTag.asString).minWidth_(80), a: \left],
								outValTxt = StaticText().string_("broadcast").align_(\left).fixedWidth_(55),

								[ StaticText().string_("Send OSC").align_(\right), a: \right],
								[ broadcastChk = CheckBox().fixedWidth_(15), a: \right],

								[ StaticText().string_("Hz").align_(\right), a: \right],
								[ updateBx = NumberBox().fixedWidth_(nBoxWidth), a: \right],

								[ StaticText().string_("Plot").align_(\right), a: \right],
								[ plotChk = CheckBox().fixedWidth_(15), a: \right],

							).margins_(2)
						).maxHeight_(45),
					).margins_(2),
					Button().states_([["+"]]).action_({this.addCtl()})
				).margins_(4).spacing_(3)
		).maxWidth_(290);

		msgTxt.action_({|txt|
			broadcastTag = (txt.value.asSymbol);
		});

		broadcastChk.action_({|chk| broadcasting = chk.value.asBoolean });
		plotChk.action_({|chk| chk.value.asBoolean.if({plotter.start},{plotter.stop}) }).value_(1);

		updateBx.action_({ |bx|
			broadcastRate = bx.value;
			broadcastWaittime = broadcastRate.reciprocal;
		}).value_(broadcastRate);

	}

	/*makeWin {

		win = Window("Broadcast Controls", Rect(0,0,320,100)).layout_(
			VLayout(
				ctlLayout = VLayout(
					View().background_(Color.rand).layout_(
						HLayout(
							[ msgTxt = TextField().string_(broadcastTag.asString).minWidth_(65), a: \left],
							nil,

							outValTxt = StaticText().string_("broadcast").align_(\left).fixedWidth_(80),
							nil,

							[ StaticText().string_("Send OSC").align_(\right), a: \right],
							[ broadcastChk = CheckBox().fixedWidth_(15), a: \right],

							[ StaticText().string_("Hz").align_(\right), a: \right],
							[ updateBx = NumberBox().fixedWidth_(nBoxWidth), a: \right],

							[ StaticText().string_("Plot").align_(\right), a: \right],
							[ plotChk = CheckBox().fixedWidth_(15), a: \right],

						).margins_(2)
					).maxHeight_(45),
				).margins_(2),
				Button().states_([["+"]]).action_({this.addCtl()})
			).margins_(4).spacing_(3)
		).onClose_({ this.free });

		msgTxt.action_({|txt|
			broadcastTag = (txt.value.asSymbol);
		});

		broadcastChk.action_({|chk| broadcasting = chk.value.asBoolean });
		plotChk.action_({|chk| chk.value.asBoolean.if({plotter.start},{plotter.stop}) }).value_(1);

		updateBx.action_({ |bx|
			broadcastRate = bx.value;
			broadcastWaittime = broadcastRate.reciprocal;
		}).value_(broadcastRate);

		win.front;
	}*/

	addPlotter { |plotLength=75, refeshRate=24|
		var view;
		plotter = ControlPlotter( busnum, 1, plotLength, refeshRate).start;
		view = plotter.mon.plotter.parent.view;
		mixView.layout.add( view.minHeight_(view.bounds.height) );

		{0.4.wait; this.updatePlotterBounds;}.fork(AppClock);
		plotterAdded = true;
	}

	updatePlotterBounds {
		var minbound, maxbound, range;
		minbound = ctlFades.collect({ |ctl| ctl.low }).minItem;
		maxbound = ctlFades.collect({ |ctl| ctl.high }).maxItem;
		range = maxbound - minbound;
		plotter.bounds_( minbound - (range * 0.25), maxbound + (range * 0.25) );
	}

	free {
		ctlFades.do(_.free);
		broadcastBus.free;
		pollTask !? { pollTask.stop.clock.clear };
	}

	prDefineColors {
		baseColor = Color.hsv(
			// Color.newHex("BA690B").asHSV;
			// Color.newHex("2C4770").asHSV;
			0.60049019607843, 0.60714285714286, 0.43921568627451, 1 );

		idColor = Color.hsv(
			*baseColor.asHSV.put( 0, (baseColor.asHSV[0] + colorShift).wrap(0,1) )
		);

		mixColor = Color.hsv(
			*idColor.asHSV
			.put(3, 0.8)
			.put(2, idColor.asHSV[2] * 1.35)
			//.put(2, (baseColor.asHSV[2] * 1.4).clip(0,1))
		);
	}

}

ControlMixMaster {
	// copyArgs
	var broadcastTags, broadcastNetAddr, broadcastRate, server;
	var <win, <mixers, mixWidth = 320;

	*new { |broadcastTags="/myControlVal", broadcastNetAddr, broadcastRate=15, server|
		^super.newCopyArgs(broadcastTags, broadcastNetAddr, broadcastRate, server).init
	}

	init {
		broadcastNetAddr ?? {broadcastNetAddr = NetAddr("localhost", NetAddr.langPort)};
		server = server ?? Server.default;

		mixers = [];

		server.waitForBoot({
			var cshift;

			cshift = rrand(-0.1, 0.1); // -0.03888, 0.093191576004028
			postf("shifting color %\n", cshift);

			this.makeWin;

			broadcastTags.asArray.do({ |tag, i|
				this.addMixer(tag, broadcastNetAddr , broadcastRate, server, (cshift*i));
			});
		});
	}

	addMixer { |sendToNetAddr, oscTag="/myControlVal", sendRate=15, server, colorShift = -0.03888|
		var mixer;
		var loadCond = Condition();
		sendToNetAddr ?? {sendToNetAddr = NetAddr("localhost", NetAddr.langPort)};
		server = server ?? Server.default;

		{
			// win.setInnerExtent( win.view.bounds.width + mixWidth );
			mixer = ControlMixer(sendToNetAddr, oscTag, sendRate, server, loadCond, colorShift);
			mixers = mixers.add(mixer);
			loadCond.wait;
			win.layout.add( mixer.mixView.postln; );
		}.fork(AppClock);
	}

	makeWin {

		win = Window("Broadcast Controls", Rect(0,0,mixWidth,100)).layout_(
			HLayout().margins_(2).spacing_(2)
		).onClose_({ this.free });

		win.front;
	}

	free {
		mixers.do(_.free);
	}

	/* Preset/Archive Support */

	prInitArchive {
		^Archive.global.put(\roverPresets, IdentityDictionary(know: true));
	}

	archive { ^Archive.global[\roverPresets] }
	presets { ^Archive.global[\roverPresets] }
	listPresets { ^this.presets.keys.asArray.sort.do(_.postln) }

	backupPreset {
		format( "cp %% %%%",
			Archive.archiveDir,
			"/archive.sctxar",
			"~/Desktop/archive.sctxar_BAK_",
			Date.getDate.stamp,
			".sctxar"
		).replace(
			" Support","\\ Support"
		).unixCmd
	}

	*backupPreset {
		format( "cp %% %%%",
			Archive.archiveDir,
			"/archive.sctxar",
			"~/Desktop/archive.sctxar_BAK_",
			Date.getDate.stamp,
			".sctxar"
		).replace(
			" Support","\\ Support"
		).unixCmd
	}


	// keyValPairs: any other data to store in the dictionary associated with this key
	// e.g. [\scenefile, "darktrees.xml"]
	storePreset { |key, overwrite =false, keyValPairs|
		var arch, synth, mixerDict, ctlInfoDict;

		arch = Archive.global[\roverPresets] ?? { this.prInitArchive };

		(arch[key].notNil and: overwrite.not).if {
			format("preset already exists! choose another name or first perform .removePreset(%)", key).throw
		};


		mixerDict = IdentityDictionary(know: true);

		mixers.do{ |mixer, i|
			var ctlFadeArr = [];
			postf("mixer %\n", i);

			// each mixer can have multiple ctlFades
			mixer.ctlFades.do{ |ctlfade, j|
				postf("\tctlfade %\n", j);
				ctlFadeArr = ctlFadeArr.add(
					IdentityDictionary( know: true ).putPairs([
						\min, ctlfade.low,
						\max, ctlfade.high,
						\signal, ctlfade.lfo,
						\freq, ctlfade.freq,
						\val, ctlfade.value,
						\scale, ctlfade.scale,
						\offset, ctlfade.offset,
						\mix, ctlfade.amp,
					])
				)
			};

			mixerDict.put( mixer.broadcastTag.asSymbol, ctlFadeArr);
		};

		arch.put( key.asSymbol ?? {Date.getDate.stamp.asSymbol},
			IdentityDictionary( know: true ).put( \mixers, mixerDict )
		);

		keyValPairs !? {
			keyValPairs.clump(2).do{ |kvArr| ctlInfoDict.put(kvArr[0].asSymbol, kvArr[1]) };
		};

		postf("Preset Stored\n%\n", key);
		arch[key].keysValuesDo{|k,v| [k,v].postln;}
	}

	prRecallCtlFaderState { | mixer, faderStates |
		var kind, ctlFade;

		faderStates.do{ |fDict, fDex|

			ctlFade = mixer.ctlFades[fDex];

			// static or lfo?
			if( fDict[\signal] == 'static' )
			{	// just recall the static val
				ctlFade.value_(fDict[\val])
			}
			{	// recall the lfo with bounds, etc...
				ctlFade.lfo_(fDict[\signal], fDict[\freq], fDict[\min], fDict[\max])
			};

			// recall mix, scale offset
			ctlFade.scale_(fDict[\scale]);
			ctlFade.offset_(fDict[\offset]);
			ctlFade.amp_(fDict[\mix]);
		}
	}

	recallPreset { |key|
		var p;
		block { |break|

			p = this.archive[key] ?? {"Preset not found".error; break.()};

			p[\mixers].keysValuesDo({ |ptag, faderStates|
				var recalled=false;
				postf("recalling mixer %\n", ptag.asString);

				fork({ var cond = Condition();
					mixers.do{ |mixer, i|

						if( mixer.broadcastTag.asSymbol == ptag ){
							// check that the current mixer has the same number of controlfaders as the preset
							var numFadersDiff = faderStates.size - mixer.ctlFades.size;

							case
							{numFadersDiff > 0}{
								// recall presets, adding numFadersDiff controls to update
								numFadersDiff.do{
									mixer.addCtl(finishCond: cond);
									cond.wait; 0.1.wait; // wait a little extra time for fade synth to start
								};
							}
							{numFadersDiff < 0}{
								numFadersDiff.abs.do{
									"removing a control".postln;
									mixer.ctlFades.last.release(freeBus: false);
									mixer.ctlFades.removeAt(mixer.ctlFades.size-1);
									mixer.ctlViews.last.remove;
								};
							};

							this.prRecallCtlFaderState( mixer, faderStates );

							recalled =true
						};
					};

					recalled.not.if{
						error( format(
							"No mixer found in the current layout to set the preset tag % not found in current setup",
							ptag ) );
						// TODO add a mixer that was not found present, remove present mixers that aren't in the preset
					};
				}, AppClock );
			});
		}
	}


	// updatePreset {
	// 	lastUpdated.notNil.if({
	// 		this.storePreset( lastRecalledSynthDex, lastUpdated, true );
	// 		},{
	// 			"last updated key is not known".warn
	// 	});
	// }


	removePreset { |key|
		Archive.global[\roverPresets][key] ?? { format("preset % not found!", key).error };
		Archive.global[\roverPresets].removeAt(key)
	}
}