cmake_minimum_required(VERSION 3.22)

project(PsyonicAbilityHandJava)

set(CMAKE_CXX_STANDARD 17)
set(lib_name ability_hand_api)

file(GLOB_RECURSE headers "ah_wrapper/include/*.h")
file(GLOB_RECURSE sources "ah_wrapper/src/*.cpp")

if(WIN32) # If on Windows
    message(STATUS "Platform is Windows")

    # Required so cmake generates a .lib file
    set(CMAKE_WINDOWS_EXPORT_ALL_SYMBOLS ON)

    # Required for compilation
    add_definitions(-DPLATFORM_WINDOWS)

    # Exclude linux specific files
    list(FILTER headers EXCLUDE REGEX ".*linux_serial\\.h")
    list(FILTER sources EXCLUDE REGEX ".*linux_serial\\.cpp")
elseif(UNIX) # If on Linux
    message(STATUS "Platform is Linux")

    # Required for compilation
    add_definitions(-DPLATFORM_LINUX)

    # Exclude Windows specific files
    list(FILTER headers EXCLUDE REGEX ".*winserial\\.h")
    list(FILTER sources EXCLUDE REGEX ".*winserial\\.cpp")
endif()

add_library(${lib_name} SHARED ${sources} ${headers})

add_subdirectory(ah_wrapper)
include_directories(${PROJECT_SOURCE_DIR}/ah_wrapper/include)

install(TARGETS ${lib_name} DESTINATION ${install_dir}lib)
install(FILES ${headers} DESTINATION ${install_dir}include)
