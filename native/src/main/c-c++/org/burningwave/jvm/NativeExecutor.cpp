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

#define GENERATE_FIELD_ACCESSOR_FUNCTIONS(typeName, fieldAccessorNamePrefix) \
JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(get ## typeName ## FieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jobject target, jobject field) { \
	return environment->fieldAccessorNamePrefix ## FieldAccessor->getValue(jNIEnv, target, field); \
} \
JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getStatic ## typeName ## FieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jclass target, jobject field) { \
	return environment->fieldAccessorNamePrefix ## FieldAccessor->getStaticValue(jNIEnv, target, field); \
} \
JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(set ## typeName ## FieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jobject target, jobject field, jobject value) { \
	environment->fieldAccessorNamePrefix ## FieldAccessor->setValue(jNIEnv, target, field, value); \
} \
JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setStatic ## typeName ## FieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jclass target, jobject field, jobject value) { \
	environment->fieldAccessorNamePrefix ## FieldAccessor->setStaticValue(jNIEnv, target, field, value); \
}

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


GENERATE_FIELD_ACCESSOR_FUNCTIONS(Object, jobject)
GENERATE_FIELD_ACCESSOR_FUNCTIONS(Short, jshort)
GENERATE_FIELD_ACCESSOR_FUNCTIONS(Integer, jint)
GENERATE_FIELD_ACCESSOR_FUNCTIONS(Long, jlong)
GENERATE_FIELD_ACCESSOR_FUNCTIONS(Float, jfloat)
GENERATE_FIELD_ACCESSOR_FUNCTIONS(Double, jdouble)
GENERATE_FIELD_ACCESSOR_FUNCTIONS(Boolean, jboolean)
GENERATE_FIELD_ACCESSOR_FUNCTIONS(Byte, jbyte)
GENERATE_FIELD_ACCESSOR_FUNCTIONS(Character, jchar)
