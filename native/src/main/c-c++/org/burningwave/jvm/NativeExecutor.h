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
#include "./../common.h"

#ifndef org_burningwave_jvm_NativeExecutor_H
	#define org_burningwave_jvm_NativeExecutor_H
	#ifndef CLASS_00001_NAME
		#define CLASS_00001_NAME org_burningwave_jvm_NativeExecutor
	#endif

	#define JNI_FUNCTION_NAME_OF_CLASS_00001(functionName) JNI_FUNCTION_NAME_OF(CLASS_00001_NAME, functionName)

	#define GENERATE_FIELD_ACCESSOR_FUNCTIONS_DECLARATION(typeName, jtype) \
	JNIEXPORT jtype JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(get ## typeName ## FieldValue0)(JNIEnv*, jobject, jobject, jobject); \
	JNIEXPORT jtype JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getStatic ## typeName ## FieldValue0)(JNIEnv*, jobject, jclass, jobject); \
	JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(set ## typeName ## FieldValue0)(JNIEnv*, jobject, jobject, jobject, jtype); \
	JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setStatic ## typeName ## FieldValue0)(JNIEnv*, jobject, jclass, jobject, jtype);

	#ifdef __cplusplus
		extern "C" {
	#endif

	JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getDeclaredField)
	  (JNIEnv*, jobject, jclass, jstring, jstring, jboolean);

	JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(allocateInstance)
	  (JNIEnv*, jobject, jclass);

	JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(throwException)
	  (JNIEnv* , jclass, jthrowable);

	GENERATE_FIELD_ACCESSOR_FUNCTIONS_DECLARATION(Object, jobject)
	GENERATE_FIELD_ACCESSOR_FUNCTIONS_DECLARATION(Short, jshort)
	GENERATE_FIELD_ACCESSOR_FUNCTIONS_DECLARATION(Integer, jint)
	GENERATE_FIELD_ACCESSOR_FUNCTIONS_DECLARATION(Long, jlong)
	GENERATE_FIELD_ACCESSOR_FUNCTIONS_DECLARATION(Float, jfloat)
	GENERATE_FIELD_ACCESSOR_FUNCTIONS_DECLARATION(Double, jdouble)
	GENERATE_FIELD_ACCESSOR_FUNCTIONS_DECLARATION(Boolean, jboolean)
	GENERATE_FIELD_ACCESSOR_FUNCTIONS_DECLARATION(Byte, jbyte)
	GENERATE_FIELD_ACCESSOR_FUNCTIONS_DECLARATION(Character, jchar)

	#ifdef __cplusplus
		}
	#endif

#endif
