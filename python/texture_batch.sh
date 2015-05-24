#!/bin/bash


# ==== laptop versions 
# python mp_vsfm_rectify.py --reorder wrap --laptop 20x30 ~/code/lightfield/data/shoots/yellowcliff/undistorted/*.jpg
# python mp_vsfm_rectify.py --laptop 20x15 ~/code/lightfield/data/shoots/dark_trees/eq_undistorted/*.jpg
# python mp_vsfm_rectify.py --reorder verticalwrap --laptop 24x24 ~/code/lightfield/data/shoots/outsidelookingin/undistorted/*.jpg
# python mp_vsfm_rectify.py --reorder wrap --laptop --inverty 30x20 ~/code/lightfield/data/shoots/bookcase/undistorted/*.jpg
# python mp_vsfm_rectify.py --reorder wrap --laptop 20x30 ~/code/lightfield/data/shoots/diningroom3/undistorted/*.jpg
# python mp_vsfm_rectify.py --reorder wrap --laptop 30x20 ~/code/lightfield/data/shoots/cliffside/undistorted/*.jpg
# python mp_vsfm_rectify.py --reorder wrap --inverty --laptop 20x30 ~/code/lightfield/data/shoots/mike1/undistorted/*.jpg
# python mp_vsfm_rectify.py --reorder wrap --laptop 20x30 ~/code/lightfield/data/shoots/mike2/undistorted/*.jpg
# python mp_vsfm_rectify.py --reorder wrap --laptop 20x30 ~/code/lightfield/data/shoots/mike3/undistorted/*.jpg
# python mp_vsfm_rectify.py --reorder verticalwrap --laptop 20x30 ~/code/lightfield/data/shoots/carkeek_night/undistorted/*.jpg
# python mp_vsfm_rectify.py --reorder wrap --laptop 20x30 ~/code/lightfield/data/shoots/carkeek/undistorted/*.jpg
# python mp_vsfm_rectify.py --reorder wrap --laptop 20x30 ~/code/lightfield/data/shoots/tunnel/undistorted/*.jpg
# python mp_vsfm_rectify.py --laptop 20x10 ~/code/lightfield/data/shoots/ballard_wall/undistorted/*.jpg
# python mp_vsfm_rectify.py --laptop 16x14 ~/code/lightfield/data/shoots/precise/undistorted/*.jpg
# python mp_vsfm_rectify.py --reorder wrap --laptop 20x15 ~/code/lightfield/data/shoots/towers/undistorted/*.jpg


# # ==== max res single texture versions
# python mp_vsfm_rectify.py --reorder wrap --single 20x30 ~/code/lightfield/data/shoots/yellowcliff/undistorted/*.jpg
# python mp_vsfm_rectify.py --single 20x15 ~/code/lightfield/data/shoots/dark_trees/eq_undistorted/*.jpg
# python mp_vsfm_rectify.py --reorder verticalwrap --single 24x24 ~/code/lightfield/data/shoots/outsidelookingin/undistorted/*.jpg
# python mp_vsfm_rectify.py --reorder wrap --single --inverty 30x20 ~/code/lightfield/data/shoots/bookcase/undistorted/*.jpg
# python mp_vsfm_rectify.py --reorder wrap --single 20x30 ~/code/lightfield/data/shoots/diningroom3/undistorted/*.jpg
# python mp_vsfm_rectify.py --reorder wrap --single 30x20 ~/code/lightfield/data/shoots/cliffside/undistorted/*.jpg
# python mp_vsfm_rectify.py --reorder wrap --inverty --single 20x30 ~/code/lightfield/data/shoots/mike1/undistorted/*.jpg
# python mp_vsfm_rectify.py --reorder wrap --single 20x30 ~/code/lightfield/data/shoots/mike2/undistorted/*.jpg
# python mp_vsfm_rectify.py --reorder wrap --single 20x30 ~/code/lightfield/data/shoots/mike3/undistorted/*.jpg
# python mp_vsfm_rectify.py --reorder verticalwrap --single 20x30 ~/code/lightfield/data/shoots/carkeek_night/undistorted/*.jpg
# python mp_vsfm_rectify.py --reorder wrap --single 20x30 ~/code/lightfield/data/shoots/carkeek/undistorted/*.jpg
# python mp_vsfm_rectify.py --reorder wrap --single 20x30 ~/code/lightfield/data/shoots/tunnel/undistorted/*.jpg
# python mp_vsfm_rectify.py --single 20x10 ~/code/lightfield/data/shoots/ballard_wall/undistorted/*.jpg
# python mp_vsfm_rectify.py --single 16x14 ~/code/lightfield/data/shoots/precise/undistorted/*.jpg
# python mp_vsfm_rectify.py --reorder wrap --single 20x15 ~/code/lightfield/data/shoots/towers/undistorted/*.jpg


# ==== full res tiled versions
# python mp_vsfm_rectify.py --reorder wrap --fullres 20x30 ~/code/lightfield/data/shoots/yellowcliff/undistorted/*.jpg
# python mp_vsfm_rectify.py --fullres 20x15 ~/code/lightfield/data/shoots/dark_trees/eq_undistorted/*.jpg
# python mp_vsfm_rectify.py --reorder verticalwrap --fullres 24x24 ~/code/lightfield/data/shoots/outsidelookingin/undistorted/*.jpg
# python mp_vsfm_rectify.py --reorder wrap --fullres --inverty 30x20 ~/code/lightfield/data/shoots/bookcase/undistorted/*.jpg
# python mp_vsfm_rectify.py --reorder wrap --fullres 20x30 ~/code/lightfield/data/shoots/diningroom3/undistorted/*.jpg
# python mp_vsfm_rectify.py --reorder wrap --fullres 30x20 ~/code/lightfield/data/shoots/cliffside/undistorted/*.jpg
# python mp_vsfm_rectify.py --reorder wrap --inverty --fullres 20x30 ~/code/lightfield/data/shoots/mike1/undistorted/*.jpg
# python mp_vsfm_rectify.py --reorder wrap --fullres 20x30 ~/code/lightfield/data/shoots/mike2/undistorted/*.jpg
# python mp_vsfm_rectify.py --reorder wrap --fullres 20x30 ~/code/lightfield/data/shoots/mike3/undistorted/*.jpg
# python mp_vsfm_rectify.py --reorder verticalwrap --fullres 20x30 ~/code/lightfield/data/shoots/carkeek_night/undistorted/*.jpg
# python mp_vsfm_rectify.py --reorder wrap --fullres 20x30 ~/code/lightfield/data/shoots/carkeek/undistorted/*.jpg
# python mp_vsfm_rectify.py --reorder wrap --fullres 20x30 ~/code/lightfield/data/shoots/tunnel/undistorted/*.jpg
# python mp_vsfm_rectify.py --fullres 20x10 ~/code/lightfield/data/shoots/ballard_wall/undistorted/*.jpg
# python mp_vsfm_rectify.py --fullres 16x14 ~/code/lightfield/data/shoots/precise/undistorted/*.jpg

# start with frame 1
# python mp_vsfm_rectify.py --reorder wrap --fullres 20x15 ~/code/lightfield/data/shoots/towers/undistorted/*.jpg



# vsfm results seem to be missing
#python mp_vsfm_rectify.py --reorder wrap --single 15x20 ~/code/lightfield/data/shoots/tivon1/undistorted/*.jpg

# multiple independent models (12)
# python mp_vsfm_rectify.py --reorder wrap --single 15x20 ~/code/lightfield/data/shoots/tivon2/undistorted/*.jpg


#eq results // yellow_trees, outsidelookingin, towers

