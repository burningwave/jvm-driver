/*
 * This file is part of Burningwave JVM driver.
 *
 * Author: Roberto Gentili
 *
 * Hosted at: https://github.com/burningwave/jvm-driver
 *
 * --
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2019-2022 Roberto Gentili
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN
 * AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */
#include "NativeExecutor.h"
#include "NativeEnvironment.h"


NativeEnvironment* environment;


JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
	JNIEnv* jNIEnv = NULL;
	if (vm->GetEnv((void**)&jNIEnv, JNI_VERSION_1_6) != JNI_OK) {
		return -1;
	}
	environment = new NativeEnvironment(jNIEnv);
	if (jNIEnv->ExceptionOccurred()) {
		return -1;
	}
	return JNI_VERSION_1_6;
}


JNIEXPORT void JNICALL JNI_OnUnload(JavaVM* vm, void* reserved) {
	JNIEnv* jNIEnv = NULL;
	vm->GetEnv((void**)&jNIEnv, JNI_VERSION_1_6);
	environment->destroy(jNIEnv);
	delete(environment);
	environment = NULL;
}


//Get/Set object value
JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getObjectFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jobject target, jobject field) {
	return environment->objectFieldAccessor->getValue(jNIEnv, target, field);
}

JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getStaticObjectFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jclass target, jobject field) {
	return environment->objectFieldAccessor->getStaticValue(jNIEnv, target, field);
}

JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setObjectFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jobject target, jobject field, jobject value) {
	environment->objectFieldAccessor->setValue(jNIEnv, target, field, value);
}

JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setStaticObjectFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jclass target, jobject field, jobject value) {
	environment->objectFieldAccessor->setStaticValue(jNIEnv, target, field, value);
}


//Get/set short value
JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getShortFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jobject target, jobject field) {
	return environment->jshortFieldAccessor->getValue(jNIEnv, target, field);
}

JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getStaticShortFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jclass target, jobject field) {
	return environment->jshortFieldAccessor->getStaticValue(jNIEnv, target, field);
}

JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setShortFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jobject target, jobject field, jobject value) {
	environment->jshortFieldAccessor->setValue(jNIEnv, target, field, value);
}

JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setStaticShortFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jclass target, jobject field, jobject value) {
	environment->jshortFieldAccessor->setStaticValue(jNIEnv, target, field, value);
}


//Get/set int value
JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getIntegerFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jobject target, jobject field) {
	return environment->jintFieldAccessor->getValue(jNIEnv, target, field);
}

JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getStaticIntegerFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jclass target, jobject field) {
	return environment->jintFieldAccessor->getStaticValue(jNIEnv, target, field);
}

JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setIntegerFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jobject target, jobject field, jobject value) {
	environment->jintFieldAccessor->setValue(jNIEnv, target, field, value);
}

JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setStaticIntegerFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jclass target, jobject field, jobject value) {
	environment->jintFieldAccessor->setStaticValue(jNIEnv, target, field, value);
}


//Get/set long value
JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getLongFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jobject target, jobject field) {
	return environment->jlongFieldAccessor->getValue(jNIEnv, target, field);
}

JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getStaticLongFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jclass target, jobject field) {
	return environment->jlongFieldAccessor->getStaticValue(jNIEnv, target, field);
}

JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setLongFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jobject target, jobject field, jobject value) {
	environment->jlongFieldAccessor->setValue(jNIEnv, target, field, value);
}

JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setStaticLongFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jclass target, jobject field, jobject value) {
	environment->jlongFieldAccessor->setStaticValue(jNIEnv, target, field, value);
}


//Get/set float value
JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getFloatFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jobject target, jobject field) {
	return environment->jfloatFieldAccessor->getValue(jNIEnv, target, field);
}

JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getStaticFloatFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jclass target, jobject field) {
	return environment->jfloatFieldAccessor->getStaticValue(jNIEnv, target, field);
}

JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setFloatFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jobject target, jobject field, jobject value) {
	environment->jfloatFieldAccessor->setValue(jNIEnv, target, field, value);
}

JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setStaticFloatFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jclass target, jobject field, jobject value) {
	environment->jfloatFieldAccessor->setStaticValue(jNIEnv, target, field, value);
}


//Get/set double value
JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getDoubleFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jobject target, jobject field) {
	return environment->jdoubleFieldAccessor->getValue(jNIEnv, target, field);
}

JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getStaticDoubleFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jclass target, jobject field) {
	return environment->jdoubleFieldAccessor->getStaticValue(jNIEnv, target, field);
}

JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setDoubleFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jobject target, jobject field, jobject value) {
	environment->jdoubleFieldAccessor->setValue(jNIEnv, target, field, value);
}

JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setStaticDoubleFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jclass target, jobject field, jobject value) {
	environment->jdoubleFieldAccessor->setStaticValue(jNIEnv, target, field, value);
}


//Get/set boolean value
JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getBooleanFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jobject target, jobject field) {
	return environment->jbooleanFieldAccessor->getValue(jNIEnv, target, field);
}

JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getStaticBooleanFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jclass target, jobject field) {
	return environment->jbooleanFieldAccessor->getStaticValue(jNIEnv, target, field);
}

JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setBooleanFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jobject target, jobject field, jobject value) {
	environment->jbooleanFieldAccessor->setValue(jNIEnv, target, field, value);
}

JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setStaticBooleanFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jclass target, jobject field, jobject value) {
	environment->jbooleanFieldAccessor->setStaticValue(jNIEnv, target, field, value);
}


//Get/set byte value
JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getByteFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jobject target, jobject field) {
	return environment->jbyteFieldAccessor->getValue(jNIEnv, target, field);
}

JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getStaticByteFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jclass target, jobject field) {
	return environment->jbyteFieldAccessor->getStaticValue(jNIEnv, target, field);
}

JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setByteFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jobject target, jobject field, jobject value) {
	environment->jbyteFieldAccessor->setValue(jNIEnv, target, field, value);
}

JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setStaticByteFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jclass target, jobject field, jobject value) {
	environment->jbyteFieldAccessor->setStaticValue(jNIEnv, target, field, value);
}


//Get/set char value
JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getCharacterFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jobject target, jobject field) {
	return environment->jcharFieldAccessor->getValue(jNIEnv, target, field);
}

JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getStaticCharacterFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jclass target, jobject field) {
	return environment->jcharFieldAccessor->getStaticValue(jNIEnv, target, field);
}

JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setCharacterFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jobject target, jobject field, jobject value) {
	environment->jcharFieldAccessor->setValue(jNIEnv, target, field, value);
}

JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setStaticCharacterFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jclass target, jobject field, jobject value) {
	environment->jcharFieldAccessor->setStaticValue(jNIEnv, target, field, value);
}

JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getDeclaredField)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jclass target, jstring name, jstring signature, jboolean isStatic) {
    const char* fieldName = jNIEnv->GetStringUTFChars(name, NULL);
    const char* fieldSignature = jNIEnv->GetStringUTFChars(signature, NULL);
    jfieldID fieldID = jNIEnv->GetFieldID(target, fieldName, fieldSignature);
    jNIEnv->ReleaseStringUTFChars(signature, fieldSignature);
    jNIEnv->ReleaseStringUTFChars(name, fieldName);
    if (!fieldID) {
        return NULL;
    }
    return jNIEnv->ToReflectedField(target, fieldID, isStatic ? JNI_TRUE : JNI_FALSE);
}

JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(allocateInstance)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jclass instanceType) {
	return jNIEnv->AllocObject(instanceType);
}

JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(throwException)(JNIEnv* jNIEnv, jclass ignored, jthrowable throwable) {
	jNIEnv->Throw(throwable);
}
