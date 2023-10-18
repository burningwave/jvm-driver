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
 * Copyright (c) 2019-2023 Roberto Gentili
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

#ifndef org_burningwave_jvm_NativeExecutor_GENERATE_FIELD_ACCESSOR_FUNCTIONS

	#define org_burningwave_jvm_NativeExecutor_GENERATE_FIELD_ACCESSOR_FUNCTIONS(typeName, jtype) \
	JNIEXPORT jtype JNICALL org_burningwave_Common_FUNCTION_NAME_OF(CLASS_00001_NAME, get ## typeName ## FieldValue0)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jobject target, jobject field) { \
		return environment->jtype ## FieldAccessor->getValue(jNIEnv, target, field); \
	} \
	JNIEXPORT jtype JNICALL org_burningwave_Common_FUNCTION_NAME_OF(CLASS_00001_NAME, getStatic ## typeName ## FieldValue0)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jclass target, jobject field) { \
		return environment->jtype ## FieldAccessor->getStaticValue(jNIEnv, target, field); \
	} \
	JNIEXPORT void JNICALL org_burningwave_Common_FUNCTION_NAME_OF(CLASS_00001_NAME, set ## typeName ## FieldValue0)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jobject target, jobject field, jtype value) { \
		environment->jtype ## FieldAccessor->setValue(jNIEnv, target, field, value); \
	} \
	JNIEXPORT void JNICALL org_burningwave_Common_FUNCTION_NAME_OF(CLASS_00001_NAME, setStatic ## typeName ## FieldValue0)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jclass target, jobject field, jtype value) { \
		environment->jtype ## FieldAccessor->setStaticValue(jNIEnv, target, field, value); \
	}

#endif

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


JNIEXPORT jobject JNICALL org_burningwave_Common_FUNCTION_NAME_OF(CLASS_00001_NAME, getDeclaredField)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jclass target, jstring name, jstring signature, jboolean isStatic) {
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


JNIEXPORT jobject JNICALL org_burningwave_Common_FUNCTION_NAME_OF(CLASS_00001_NAME, allocateInstance)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jclass instanceType) {
	return jNIEnv->AllocObject(instanceType);
}


JNIEXPORT void JNICALL org_burningwave_Common_FUNCTION_NAME_OF(CLASS_00001_NAME, throwException)(JNIEnv* jNIEnv, jclass ignored, jthrowable throwable) {
	jNIEnv->Throw(throwable);
}


org_burningwave_jvm_NativeExecutor_GENERATE_FIELD_ACCESSOR_FUNCTIONS(Object, jobject)
org_burningwave_jvm_NativeExecutor_GENERATE_FIELD_ACCESSOR_FUNCTIONS(Short, jshort)
org_burningwave_jvm_NativeExecutor_GENERATE_FIELD_ACCESSOR_FUNCTIONS(Integer, jint)
org_burningwave_jvm_NativeExecutor_GENERATE_FIELD_ACCESSOR_FUNCTIONS(Long, jlong)
org_burningwave_jvm_NativeExecutor_GENERATE_FIELD_ACCESSOR_FUNCTIONS(Float, jfloat)
org_burningwave_jvm_NativeExecutor_GENERATE_FIELD_ACCESSOR_FUNCTIONS(Double, jdouble)
org_burningwave_jvm_NativeExecutor_GENERATE_FIELD_ACCESSOR_FUNCTIONS(Boolean, jboolean)
org_burningwave_jvm_NativeExecutor_GENERATE_FIELD_ACCESSOR_FUNCTIONS(Byte, jbyte)
org_burningwave_jvm_NativeExecutor_GENERATE_FIELD_ACCESSOR_FUNCTIONS(Character, jchar)
