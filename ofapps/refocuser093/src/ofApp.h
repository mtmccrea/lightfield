#pragma once

#include "ofxXmlSettings.h"
#include "ofMain.h"
#include "ofxOsc.h"

#define MAX_SUBIMAGES 600
#define MAX_OFFSETS 1200

class ofApp : public ofBaseApp{
public:

    void setup();
    void update();
    void draw();

    void loadNewScene(int newscenenum);
    void loadNewScene(string scenefile);
    
    void loadDirectory(string path);
    void loadXMLSettings(string settingsfile);
    void loadXMLScene(string scenefile);
    vector<string> scenefiles;

    void loadLightfieldData();
    void freeLightfieldData();
    void setupGraphics();

//    void updateAperture();
//   ofFbo aperture_mask_tex;
//    float *aperture_mask;

    void snapshot();
    string startTimeStamp;

    void keyPressed  (int key);
    void keyReleased(int key);
    int mouseXStart, mouseYStart;
    float xoffsetStart, yoffsetStart;
    float focusStart, zoomStart;
    int xcountStart, ycountStart;
    int xstartStart, ystartStart;
    bool bPressed;


    void process_OSC(ofxOscMessage m);

    ofxOscReceiver receiver;
    int port;

	// lf texture files
    ofTexture lfplane;
    string lffilename;

	// data textures for shader
    ofFbo campos_tex;
    ofFbo subimg_corner_tex;


    // render fbos as pointers
    ofPtr <ofFbo> fbo;
    vector <ofPtr <ofFbo> > refocusFbo;
    ofPtr <ofFbo> maskFbo;

	// refocus shaders
    ofShader shader;

	// refocus parameters
    float focus;
    float zoom;
    int xcount, ycount, xstart, ystart;

    float minScale, maxScale;
    float xoffset, yoffset;

    float fade;

    // camera images
    int xsubimages, ysubimages, subwidth, subheight;

    float offsets[MAX_OFFSETS];

    // snapshot
    int snapcount;

    // onscreen display
    bool bShowThumbnail;
    bool bHideCursor;
    bool bDebug;
    bool bFill;
    
    // physical setup
    float screen_width;
    float screen_height;

    
	// rendering control
    bool bSuspendRender;
    
    // we will have a dynamic number of scenes, based on the content of a directory:
    ofDirectory dir;
    int scenenum;
};
