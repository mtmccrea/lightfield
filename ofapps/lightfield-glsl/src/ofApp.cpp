#include "ofApp.h"

#define STRINGIFY(A) #A


void ofApp::loadImages() {
    
//    lfplane.loadImage("home3_contact0.jpg");
    //    lfplane.loadImage("precise_rect5.jpg");
//    lfplane.loadImage("chessboard_2_200_negZ.jpg");
//    lfplane.loadImage("chessboard_2_1_negZ.jpg");
//    lfplane.loadImage("chessboard_2_1.jpg");
//    lfplane.loadImage("chessboard_2_1_negy_negz.jpg");
//    lfplane.loadImage("chessboard_4_1_negy_negz.jpg");
//    lfplane.loadImage("chessboard_4_1_negz.jpg");
//     lfplane.loadImage("chessboard_4_1.jpg");
//    lfplane.loadImage("chessboard_4_1_negx.jpg");
//    lfplane.loadImage("chessboard_4_1_rinv.jpg");
//    lfplane.loadImage("chessboard_4_f1_negz.jpg");
//    lfplane.loadImage("darktrees_contact_0.8.jpg");
//    lfplane.loadImage("darktrees_contact_eq_0.8.jpg");

//    lfplane.loadImage("towers_eq_0.8.jpg");
    lfplane.loadImage("towers_0.8.jpg");
    
    sourceWidth=lfplane.getWidth();
    sourceHeight=lfplane.getHeight();
    
    cout << "IMAGE WIDTHS " << sourceWidth << ", " << sourceHeight << endl;
    
}
//--------------------------------------------------------------
void ofApp::setup(){
    
    ofEnableAlphaBlending();
    
    subwidth 		= 819;//564;//1024;//562; 963;//
    subheight 		= 614;//317;//768;//316; 642;//
    
    xsubimages = 20;//29; // 17;//29;//16; //
    ysubimages = 15;//20; // 17;//20;//14; //
    
    minScale = -32;//-1500;
    maxScale = 8;//1500;
    
    xstart = 0;
    ystart = 0;
    xcount = xsubimages;
    ycount = ysubimages;
    

    // display variables
    bDrawThumbnail = true;
    bHideCursor = false;
    bDebug = false;
    
    loadImages();
    
    fbo.allocate(subwidth,subheight);
    maskFbo.allocate(subwidth,subheight);
    
    string shaderProgram = STRINGIFY(
                                     uniform sampler2DRect lfplane;
                                     uniform sampler2DRect lfplane2;
                                     uniform sampler2DRect maskTex;
                                     
                                     uniform float xres;
                                     uniform float yres;
                                     uniform int xcount;
                                     uniform int ycount;
                                     uniform int xstart;
                                     uniform int ystart;
                                     uniform float scale;
                                     uniform float offset;
                                     uniform int count;
                                     
                                     void main (void){
                                         vec2 pos = gl_TexCoord[0].st;
                                         
                                         vec4 mask = texture2DRect(maskTex, pos);
                                         
                                         vec4 color = vec4(0,0,0,0);
                                         float halfxcount = float(xcount) / 2.0;
                                         float halfycount = float(ycount) / 2.0;
                                         for (int x=xstart; x<xstart+xcount; x++){		// For each microimage in
                                             for (int y=ystart; y<ystart+ycount; y++) {
                                                 
//                                                 vec2 subpos = pos + vec2(float(x)*xres, float(y)*yres) + vec2(scale * 4.0, scale * -4.0);
                                                vec2 subpos = pos + vec2(float(x)*xres, float(y)*yres);
                                                 
                                                 color += texture2DRect(lfplane, subpos+vec2( (1.0+(offset/xres)) * scale*(float(x) - halfxcount) + 100.0, (1.0+(offset/yres))*scale*(float(y)-halfycount)));
                                                 
                                                 // 0.562
                                                 //color += texture2DRect(textures[i], pos+vec2(scale*float(i), 0)) * weight;
                                             }
                                         }
                                         
                                         //color = (rTxt + gTxt + bTxt) / 3.0;
                                         color = color / float(xcount*ycount);
                                         
                                         gl_FragColor = color;
                                     }
                                     );
    
    shader.setupShaderFromSource(GL_FRAGMENT_SHADER, shaderProgram);
    shader.linkProgram();
    
    // Let�s clear the FBO�s
    // otherwise it will bring some junk with it from the memory
    fbo.begin();
    ofClear(0,0,0,255);
    fbo.end();
    
    maskFbo.begin();
    ofClear(0,0,0,255);
    maskFbo.end();
    
    shader.begin();
    
    shader.setUniformTexture("lfplane", lfplane, 1);

    shader.end();
    
    GLint maxTextureSize;
    glGetIntegerv(GL_MAX_TEXTURE_SIZE, &maxTextureSize);
    std::cout <<"Max texture size: " << maxTextureSize << std::endl;
}

//--------------------------------------------------------------
void ofApp::update(){
    
    // This just
    maskFbo.begin();
    ofClear(255, 0, 0,255);
    //    multimaskImg.draw( mouseX-multimaskImg.getWidth()*0.5, 0 );
    maskFbo.end();
    
    // MULTITEXTURE MIXING FBO

    fbo.begin();
    ofClear(0, 0, 0,255);
    shader.begin();
    
//    shader.setUniformTexture("lfplane", lfplane, 1);
    
    shader.setUniform1f("scale", synScale);
    shader.setUniform1i("xstart", xstart);
    shader.setUniform1i("ystart", ystart);
    shader.setUniform1i("xcount", xcount);//lfstack.size());
    shader.setUniform1i("ycount", ycount);
    shader.setUniform1f("xres", subwidth);//562);
    shader.setUniform1f("yres", subheight);//316);
    shader.setUniform1f("offset", 0.0);
    
    maskFbo.draw(0,0);
    
    shader.end();
    fbo.end();
    
    ofSetWindowTitle( ofToString( ofGetFrameRate()));
}

//--------------------------------------------------------------
void ofApp::draw(){
    ofBackground(ofColor::black);
    
    // thumbnail size
    float tWidth = 160;//320;
    float tHeight = 90;//180;
    
    // fused image size
    float height = ofGetWindowHeight();
    float width = height*(sourceWidth/sourceHeight);
    
    ofSetColor(255);
    
    // draw fused image
    fbo.draw(0, 0, width, height);

    // draw thumbnail with indicator
    if(bDrawThumbnail) {
        ofSetColor(255);
        lfplane.draw(5,5,tWidth,tHeight);
        ofSetColor(255, 0, 0);
        ofNoFill();
        float xunit = tWidth/xsubimages;//29.;
        float yunit = tHeight/ysubimages;//20.0;
        ofRect(5+xstart*xunit, 5+ystart*yunit, xcount*xunit, ycount*yunit);
    }
    
    if(bDebug) {
        ofSetColor(255);
        ofDrawBitmapString(ofToString(synScale), 10, ofGetHeight() - 10);
    }
}

//--------------------------------------------------------------
void ofApp::keyPressed(int key){
    if(key=='f')
        ofToggleFullscreen();
    if(key=='z') {
        xstart = ofMap(mouseX, 0, ofGetWindowWidth(), 0, xsubimages-xcount+1);
        ystart = ofMap(mouseY, 0, ofGetWindowHeight(), 0, ysubimages-ycount+1);
    }
    if(key=='x') {
        xcount = ofMap(mouseX, 0, ofGetWindowWidth(), 0, xsubimages);
        ycount = ofMap(mouseY, 0, ofGetWindowHeight(), 0, ysubimages);
        if(xcount+xstart > xsubimages)
            xstart = xsubimages - xcount;
        if(ycount + ystart > ysubimages)
            ystart = ysubimages - ycount;
    }
    if(key == 'c')
        synScale = ofMap(mouseX, 0, ofGetWindowWidth(), minScale, maxScale);//, -2.5);
    if(key == 't')
        bDrawThumbnail = !bDrawThumbnail;
    if(key == 'm') {
        bHideCursor = !bHideCursor;
        if (bHideCursor) {
            ofHideCursor();
        } else {
            ofShowCursor();
        };
    }
    if(key == 'd')
        bDebug = !bDebug;
    
}

//--------------------------------------------------------------
void ofApp::keyReleased(int key){
    
}

//--------------------------------------------------------------
void ofApp::mouseMoved(int x, int y ){
    
}

//--------------------------------------------------------------
void ofApp::mouseDragged(int x, int y, int button){
    
}

//--------------------------------------------------------------
void ofApp::mousePressed(int x, int y, int button){
    
}

//--------------------------------------------------------------
void ofApp::mouseReleased(int x, int y, int button){
    
}

//--------------------------------------------------------------
void ofApp::windowResized(int w, int h){
    
}

//--------------------------------------------------------------
void ofApp::gotMessage(ofMessage msg){
    
}

//--------------------------------------------------------------
void ofApp::dragEvent(ofDragInfo dragInfo){ 
    
}