cmake_minimum_required(VERSION 2.8)
project( featureGetter )
find_package( OpenCV REQUIRED )
add_executable( featureGetter featureGetter.cpp )
target_link_libraries( featureGetter ${OpenCV_LIBS} )
