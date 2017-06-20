//==========================================================================
// 2015/08/31: yctung: add this new test for libSVM in jni interface 
//==========================================================================

#include <jni.h>
#include <string.h>
#include <android/log.h>
#include <stdarg.h>
#include <stdio.h>
#include <stdlib.h>
#include <vector>
#include "./libsvm/svm-train.h"
#include "./libsvm/svm-predict.h"
#include "./libsvm/svm-scale.h"
#include "common.h"

// helper function to be called in Java for making svm-scale
extern "C" void Java_com_example_fud_sp_MainActivity_jniSvmScale(JNIEnv *env, jobject obj, jstring cmdIn, jstring fileOutPathIn){
	const char *cmd = env->GetStringUTFChars(cmdIn, 0);
	const char *fileOutPath = env->GetStringUTFChars(fileOutPathIn, 0);
	debug("jniSvmScale cmd = %s, fileOutPath = %s", cmd, fileOutPath);

	std::vector<char*> v;

	// add dummy head to meet argv/command format
	std::string cmdString = std::string("dummy ")+std::string(cmd);

	cmdToArgv(cmdString, v);

	// hack to redirect the std output of svm-scale to a file
	// credit: https://stackoverflow.com/questions/10150468/how-to-redirect-cin-and-cout-to-files
	freopen(fileOutPath,"w", stdout);

	// make svm train by libsvm
	svmscale::main(v.size(),&v[0]);

	// close the redirect file(stdout)
	// TODO: should we get the stdout back? seems not necessary in this case
	fclose (stdout);


	// free vector memory
	for(int i=0;i<v.size();i++){
		free(v[i]);
	}

	// free java object memory
	env->ReleaseStringUTFChars(cmdIn, cmd);
	env->ReleaseStringUTFChars(fileOutPathIn, fileOutPath);
}


// helper function to be called in Java for making svm-train
extern "C" void Java_com_example_fud_sp_MainActivity_jniSvmTrain(JNIEnv *env, jobject obj, jstring cmdIn){
	const char *cmd = env->GetStringUTFChars(cmdIn, 0);
	debug("jniSvmTrain cmd = %s", cmd);

	std::vector<char*> v;

	// add dummy head to meet argv/command format
	std::string cmdString = std::string("dummy ")+std::string(cmd);

	cmdToArgv(cmdString, v);

	// make svm train by libsvm
	svmtrain::main(v.size(),&v[0]);


	// free vector memory
	for(int i=0;i<v.size();i++){
		free(v[i]);
	}

	// free java object memory
	env->ReleaseStringUTFChars(cmdIn, cmd);
}

// helper function to be called in Java for making svm-predict
extern "C" void Java_com_example_fud_sp_MainActivity_jniSvmPredict(JNIEnv *env, jobject obj, jstring cmdIn){
	const char *cmd = env->GetStringUTFChars(cmdIn, 0);
	debug("jniSvmPredict cmd = %s", cmd);

	std::vector<char*> v;

	// add dummy head to meet argv/command format
	std::string cmdString = std::string("dummy ")+std::string(cmd);

	cmdToArgv(cmdString, v);

	// make svm train by libsvm
	svmpredict::main(v.size(),&v[0]);


	// free vector memory
	for(int i=0;i<v.size();i++){
		free(v[i]);
	}

	// free java object memory
	env->ReleaseStringUTFChars(cmdIn, cmd);
}



/*
*  just some test functions -> can be removed
*/
extern "C" JNIEXPORT int JNICALL Java_com_example_fud_sp_MainActivity_testInt(JNIEnv * env, jobject obj){
	return 5566;
}

extern "C" void Java_com_example_fud_sp_MainActivity_testLog(JNIEnv *env, jobject obj, jstring logThis){
	const char * szLogThis = env->GetStringUTFChars(logThis, 0);
	debug("%s",szLogThis);

	env->ReleaseStringUTFChars(logThis, szLogThis);
} 
