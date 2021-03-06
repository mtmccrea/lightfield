// SCORING

//get audio initialized
s.waitForBoot({
	var intSndPath, extSndPath;

	1.wait;
	~eq = MasterEQ(4,s);

	/* added!! */
	~motorBuf = CtkBuffer.playbuf("/Users/dxlocal/Documents/ROVER/Rover_audio/EDITS/dining_room_capture_EDIT.WAV").load;
	s.sync;
	~motorPB = MotorPlayback(0, ~motorBuf);
	s.sync;


	~gs1 !? {~gs1.free};
	~gs2 !? {~gs2.free};

		/* source file */
	intSndPath = "/Users/dxlocal/Documents/ROVER/Rover_audio/EDITS/around_the_dining_room_morning_EDIT.WAV";

	/* GrainScanner1 with 3 GrainScan1's */
	~gs1 = GrainScanner1( 3, intSndPath );
	~gs1.presetGUI(3);
	3.wait;

	extSndPath = "/Users/dxlocal/Documents/ROVER/Rover_audio/EDITS/discovery_cliffside_1_EDIT.WAV";

	~gs2 = GrainScanner2( 0, extSndPath );
	5.wait;
	3.do{|i|~gs2.addScanner(i)}; // add scanners
	~gs2.presetGUI(5);


	~imageCtl = ControlMixMaster(
		["focus", "zoom", "xscroll", "yscroll", "xstart", "ystart", "xcount", "ycount" ],
		NetAddr("refocuser.local", 12345));
	// NetAddr("192.168.2.2", 12345));
	// NetAddr("192.168.0.100", 12345));

	// NetAddr("localhost", 12345));
	2.wait;
	~imageCtl.mixers.do({ |mxr| mxr.plotter.stop });

	~fader=ControlFade(1.0);
	1.wait;
	~fader.broadcast( ~imageCtl.broadcastNetAddr, '/fade', 30 );
	// ~fader.fadeTime_(6.0);
	// ~fader.value_(1.0)
	// ~fader.value_(0.0)


	// interiors
	~interiors = [
		"./textures/bookcase.xml",
		"./textures/diningroom3.xml",
		"./textures/mike1.xml",
		"./textures/mike3.xml"
	];

	// departures
	~departures = [
		"./textures/dark_trees.xml",
		"./textures/carkeek.xml",
		"./textures/cliffside.xml",
		"./textures/yellowcliff.xml",
		"./textures/tunnel.xml",
		"./textures/carkeek_night.xml",

	];

	// portals, liminal spaces
	~portals = [
		"./textures/mike2.xml",
		"./textures/outsidelookingin.xml"
	];

	~audioSceneDict = IdentityDictionary(know: true).putPairs([
		/* interiors */
		\diningroom3, \int_,
		\mike1, \int_,
		\mike3, \int_,
		\bookcase, \int_,

		/* exteriors */
		\dark_trees, \nightCD_,
		\carkeek, \sea_,
		\cliffside, \cliff1_,
		\tunnel, \sea_,
		\carkeek_night, \sea_,
		\yellowcliff, \yellow_,

		/* liminal spaces */
		\outsidelookingin, \night_,
		\mike2, \garden_,

	]);


	~sceneScore = [
		// scene params
		//scene, dur, numViews
		// int
		["bookcase", 240, 5],
		// ext
		["cliffside", 120, 3],
		// ext
		["carkeek", 120, 3],
		// int
		["diningroom3", 240, 5],
		// ext
		["tunnel", 150, 3],
		// int
		["mike1", 240, 5],
		// lim
		["outsidelookingin", 130, 3],
		// ext
		["dark_trees", 120, 3],
		// int
		["mike3", 240, 5],
		// ext
		["carkeek_night", 120, 2],
		// ext
		["yellowcliff", 90, 2],
		// lim
		["mike2", 90, 3],
	];

	// need to edit:
	//   yellowcliff
	//   bookcase

	~sceneViews = IdentityDictionary(know: true).putPairs([
		\bookcase,
		[
			\bookcase_bed_knob_plant, 10.0, 7.0, // roll right slightly!
			\bookcase_close, 7.0, 5.0, // focus through it! turn books into abstraction
			\bookcase_mirror_flat, 10.0, 7.0, // good
			// \bookcase_mirror_rover, 5.0, 5.0,
			\bookcase_start, 10.0, 15.0, // still? move on the bush to the left
			\bookcase_window, 5.0, 5.0, // too similar to the one below
			\bookcase_rover_mobile_selfie, 5.0, 30.0, //
		],
		\carkeek,
		[
			\carkeek_tornado, 5.0, 5.0,
			\carkeek_train1, 5.0, 5.0,
			\carkeek_gravel, 5.0, 5.0 // push the abstraction! crop just the gravel
			\carkeek_pile_distant, 5.0, 5.0,
			// zoom into guts of the pile
		],
		\carkeek_night,
		[
			\carkeek_night_ground, 5.0, 5.0,
			\carkeek_night_shore, 5.0, 5.0, // smoothly move left right. focus swim.
			\carkeek_night_hypercolortree, 5.0, 5.0,
			// \carkeek_night_oceanblur, 5.0, 5.0,
		],
		\cliffside,
		[
			\cliffside_shore, 10.0, 25.0, // just slower
			\cliffside_zoom, 5.0, 5.0, //
			\cliffside_trees_breathing, 35.0, 15.0,
			\cliffside_right, 10.0, 20.0,
			// need 1 or 2 better scenes
		],
		\dark_trees,
		[
			// \dark_trees_start, 5.0, 5.0,
			\dark_trees_leaves, 5.0, 5.0, // more images to pop leaves more
			\dark_trees_whitetree, 5.0, 5.0, // lateral movement from leftmost trunk
			\dark_trees_deepzoom, 5.0, 5.0, // run for a long time
		],
		\diningroom3,
		[
			\diningroom3_kaleid, 15.0, 15.0,
			\diningroom3_detail_books_nofocus, 10.0, 5.0,
			\diningroom3_detail_books_focus, 5.0, 5.0,
			// \diningroom3_books3, 5.0, 5.0,
			\diningroom3_flower, 5.0, 10.0, // different oscillator. too perfect on flower
			// more lfo displacement
			\diningroom3_floor, 10.0, 10.0
		],
		\mike1,
		[
			\mike1_start, 10.0, 20.0, // bump it off center
			\mike1_3, 5.0, 5.0, // horizontal
			\mike1_5_fruitbowl, 25.0, 15.0,
			\mike1_parallax_left, 5.0, 5.0, // vertical expansion
			\mike1_window, 5.0, 5.0, // one of
			\mike1_3_rover, 5.0, 5.0, // these should
			\mike1_mirrorimage_moving, 5.0, 5.0, // go. good
			// spend some time with the books
			// too many mirror shots, too many rover
		],
		\mike2,
		[
			\mike2_rover, 5.0, 5.0,
			\mike2_swimmingtrees, 3.0, 20.0, // not enough swim!
			// \mike2_window, 5.0, 5.0, // too similar!
			\mike2_strings, 15.0, 5.0, // too similar!
			\mike2_desk_ironingboard, 10.0, 30.0, // **
		],
		\mike3,
		[
			\mike3_book_orbit1, 4.0, 1.0,
			\mike3_cloth_table, 5.0, 5.0, // stay right!
			\mike3_distant_window, 10.0, 15.0, // **
			\mike3_fence, 10.0, 10.0, // *
			\mike3_lamp_glow, 5.0, 5.0,
			\mike3_peeker, 10.0, 15.0,
			\mike3_sputnik, 10.0, 5.0,
			\mike3_start, 10.0, 10.0, // still image?
			\mike3_fruittable, 10.0, 5.0, // *** gauzy
			\mike3_windowplane, 10.0, 10.0, // **
		],
		\outsidelookingin,
		[
			\outside_1, 5.0, 5.0, // totally still?
			\outside_2, 5.0, 5.0,
			\outside_book, 5.0, 5.0, // add verry slow drift up. pan down drift up very slowly. straight
			\outside_book_left, 5.0, 5.0,
			\outside_bookshelf_lamp, 5.0, 5.0,
			// \outside_book, 5.0, 5.0,
			// \outside_book_left, 5.0, 5.0,

			// add lamp focus scene
		],
		\tunnel,
		[
			\tunnel_tunnels, 5.0, 5.0,
			\tunnel_start, 5.0, 5.0, // worship the tree! crop top 2/3rds
			\tunnel_rocks, 5.0, 5.0, // slow the oscillator
			\tunnel_textures, 5.0, 5.0 // dope textures. get rid of bars
		],
		\yellowcliff,
		[
			\yellowcliff_bush, 5.0, 5.0,
			\yellowcliff_sky_top, 5.0, 5.0,
			\yellowcliff_sky_bot, 5.0, 5.0,
			\yellowcliff_bush_left, 5.0, 5.0,
			\yellowcliff_bush_right, 5.0, 5.0,
			\yellowcliff_close, 5.0, 5.0,
		],
	]);

	~scoreTask !? {~scoreTask.stop.clock.clear};

	~scoreTask = Task({
		var name, fade, waittime;

		inf.do { |i|
			// start with second scene for testing
			// ~sceneScore = ~sceneScore.rotate(-1);

			~sceneScore.do{ |sceneParams|
				var scene, dur, nViews, sceneFileName, audioScene, whichScanner;
				var fadeOut = 3, fadeIn = 3, viewPresets, sceneType;
				var tempPreset, tempTransition, tempLinger;
				var motorDur = 45, motorEnv = [0.4,0.2,0.4];
				#scene, dur, nViews = sceneParams;

				// shorten times for testing
				// dur = dur * 0.5;
				// motorDur = motorDur * 0.5;

				// fade out
				~fader.fadeTime_(fadeOut);
				~fader.value_(1.0);
				[~gs1, ~gs2].do(_.release(fadeOut));
				(fadeOut+1).wait;

				// load scene
				sceneFileName = format("./textures/%.xml", scene);
				~imageCtl.loadScene(format("./textures/%.xml", scene));
				~interiors.collect(_.asSymbol).includes(sceneFileName.asSymbol).if({
					"new scene is INTERIOR".postln;
					sceneType = \int;
					},{
						"new scene is EXTERIOR".postln;
						sceneType = \ext;
				});

				// wait for the scene file to load and motor sound to begin fading
				// to cue next view
				(motorDur*motorEnv[0] - fadeOut - fadeIn -3).wait;

				// fade in
				~fader.fadeTime_(fadeIn+2);
				~fader.value_(0.0);

				fork{
					(dur - (motorDur*motorEnv[0])).wait;

					~motorPB.play(motorDur);
					~motorPB.synth
					.fadeIn_(motorEnv[0]).sustain_(motorEnv[1]).fadeOut_(motorEnv[2])
					.amp_(-2.dbamp)
					.cutoffHigh_(16000).cutoffLow_(800)
					.onsetCurveIn_(5).onsetCurveOut_(-5)
					.ampCurveIn_(3).ampCurveOut_(-3)
					.mixLow_(0.1).mixHigh_(1)
					.t60low_(6).t60high_(4);
				};

				// choose the views within the scene
				// viewPresets = ~videoPresets.scramble.keep(nViews);
				viewPresets = ~sceneViews[scene.asSymbol].clump(3).scramble.keep(nViews);
				// nViews = ~sceneViews[scene.asSymbol].clump(3).size;

				viewPresets.do { |vPreset, viewCount|
					var viewDur, transition, linger;
					viewDur = dur/nViews; // equal time on each view
					transition = 0.0;//rrand(0.3, 0.7) * viewDur;
					linger = viewDur - transition;

					switch( sceneType,
						\int, {
							audioScene = ~gs1.presets.keys.choose; // choose between interiors
							whichScanner = ~gs1;
						},
						\ext, {
							var audioKey;
							audioKey = ~audioSceneDict[scene.asSymbol];
							postf("audioKey: %\n", audioKey);
							audioScene = GrainScanner2.presets.keys.asArray.collect(_.asString).select({
								|pset| pset.contains(audioKey.asString)}).choose.asSymbol;
							whichScanner = ~gs2;
						}
					);

					postf("chose audio scene %\n", audioScene);

					if(viewCount == 0)
					{	// first view in new scene

						// recalling audio scene immediately for first view
						whichScanner.recallPreset( audioScene, 0 );

						// wait for audio scene to load if exterior
						if( sceneType == \ext )
						{ "forking off".postln;
							fork({ 4.5.wait;
								 postf("playing interior % over %", whichScanner, fadeIn);
								whichScanner.play(fadeIn);}, AppClock)
						}
						{  postf("playing interior % over %", whichScanner, fadeIn);
							whichScanner.play(fadeIn)
						};

						// play the appropriate scanner

						// if interior, send to reverb after transition
						if( sceneType == \int ){
							fork{ ~gs1.sendToReverb( dur * 0.8)} };
					}{
						postf("recalling audio scene over % seconds\n", transition);
						whichScanner.recallPreset(audioScene, transition);
					};


					// RECALL video

					#tempPreset, tempTransition, tempLinger = vPreset;

					// postf("recalling scene %, preset %\n", scene, vPreset);
					// ~imageCtl.recallPreset(vPreset, transition);
					postf("recalling scene %, preset %\n", scene, tempPreset);
					~imageCtl.recallPreset(tempPreset, transition);

					postf("waiting % seconds for view to play out\n", viewDur);

					(transition + linger).wait;
				};

			};
		}
	}, AppClock);


	1.wait;
	~scoreTask.play;
	// ~scoreTask.stop
});
