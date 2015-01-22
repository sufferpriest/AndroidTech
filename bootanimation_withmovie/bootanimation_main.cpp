/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#define LOG_TAG "BootAnimation"

#include <cutils/properties.h>

#include <binder/IPCThreadState.h>
#include <binder/ProcessState.h>
#include <binder/IServiceManager.h>

#include <utils/Log.h>
#include <utils/threads.h>

#if defined(HAVE_PTHREADS)
# include <pthread.h>
# include <sys/resource.h>
#endif

#include "BootAnimation.h"

using namespace android;

// ---------------------------------------------------------------------------

int main(int argc, char** argv)
{
    char value[PROPERTY_VALUE_MAX];
    property_get("debug.sf.nobootanimation", value, "0");
    int noBootAnimation = atoi(value);
    if(noBootAnimation != 0) {
    ALOGI_IF(noBootAnimation,  "boot animation disabled");  //Jelly Bean changed
    }
    if (!noBootAnimation) {

        sp<ProcessState> proc(ProcessState::self());
        ProcessState::self()->startThreadPool();

        // create the boot animation object
        bool setBoot = true;
		bool setRotated = false;
		bool sePaly = true;
		if(argc > 1){
           if(!strcmp(argv[1],"shut"))
		   	setBoot = false;
		}
				
		if(argc > 3){
			if(!strcmp(argv[3],"rotate"))
		   	setRotated = true;
		}

        sp<BootAnimation> boot = new BootAnimation(setBoot,sePaly,setRotated);
        IPCThreadState::self()->joinThreadPool();
    }
    return 0;
}
