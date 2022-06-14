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

	#define org_burningwave_jvm_NativeExecutor_GENERATE_FIELD_ACCESSOR_FUNCTIONS_DECLARATION(typeName, jtype) \
	JNIEXPORT jtype JNICALL org_burningwave_Common_FUNCTION_NAME_OF(CLASS_00001_NAME, get ## typeName ## FieldValue0)(JNIEnv*, jobject, jobject, jobject); \
	JNIEXPORT jtype JNICALL org_burningwave_Common_FUNCTION_NAME_OF(CLASS_00001_NAME, getStatic ## typeName ## FieldValue0)(JNIEnv*, jobject, jclass, jobject); \
	JNIEXPORT void JNICALL org_burningwave_Common_FUNCTION_NAME_OF(CLASS_00001_NAME, set ## typeName ## FieldValue0)(JNIEnv*, jobject, jobject, jobject, jtype); \
	JNIEXPORT void JNICALL org_burningwave_Common_FUNCTION_NAME_OF(CLASS_00001_NAME, setStatic ## typeName ## FieldValue0)(JNIEnv*, jobject, jclass, jobject, jtype);

	#ifdef __cplusplus
		extern "C" {
	#endif

	JNIEXPORT jobject JNICALL org_burningwave_Common_FUNCTION_NAME_OF(CLASS_00001_NAME, getDeclaredField)
	  (JNIEnv*, jobject, jclass, jstring, jstring, jboolean);

	JNIEXPORT jobject JNICALL org_burningwave_Common_FUNCTION_NAME_OF(CLASS_00001_NAME, allocateInstance)
	  (JNIEnv*, jobject, jclass);

	JNIEXPORT void JNICALL org_burningwave_Common_FUNCTION_NAME_OF(CLASS_00001_NAME, throwException)
	  (JNIEnv* , jclass, jthrowable);

	org_burningwave_jvm_NativeExecutor_GENERATE_FIELD_ACCESSOR_FUNCTIONS_DECLARATION(Object, jobject)
	org_burningwave_jvm_NativeExecutor_GENERATE_FIELD_ACCESSOR_FUNCTIONS_DECLARATION(Short, jshort)
	org_burningwave_jvm_NativeExecutor_GENERATE_FIELD_ACCESSOR_FUNCTIONS_DECLARATION(Integer, jint)
	org_burningwave_jvm_NativeExecutor_GENERATE_FIELD_ACCESSOR_FUNCTIONS_DECLARATION(Long, jlong)
	org_burningwave_jvm_NativeExecutor_GENERATE_FIELD_ACCESSOR_FUNCTIONS_DECLARATION(Float, jfloat)
	org_burningwave_jvm_NativeExecutor_GENERATE_FIELD_ACCESSOR_FUNCTIONS_DECLARATION(Double, jdouble)
	org_burningwave_jvm_NativeExecutor_GENERATE_FIELD_ACCESSOR_FUNCTIONS_DECLARATION(Boolean, jboolean)
	org_burningwave_jvm_NativeExecutor_GENERATE_FIELD_ACCESSOR_FUNCTIONS_DECLARATION(Byte, jbyte)
	org_burningwave_jvm_NativeExecutor_GENERATE_FIELD_ACCESSOR_FUNCTIONS_DECLARATION(Character, jchar)

	#ifdef __cplusplus
		}
	#endif

#endif
