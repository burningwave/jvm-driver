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
#include "NativeEnvironment.h"

#ifndef org_burningwave_jvm_NativeEnvironment_GENERATE_FIELD_ACCESSOR_INITIALIZATOR

	#define org_burningwave_jvm_NativeEnvironment_GENERATE_FIELD_ACCESSOR_INITIALIZATOR(typeName, jtype) \
	this->jtype ## FieldAccessor = new FieldAccessor<jtype>( \
		&JNIEnv::Get ## typeName ## Field, &JNIEnv::GetStatic ## typeName ## Field, \
		&JNIEnv::Set ## typeName ## Field, &JNIEnv::SetStatic ## typeName ## Field \
	);

#endif


#ifndef org_burningwave_jvm_NativeEnvironment_GENERATE_FIELD_ACCESSOR_DESTRUCTOR

	#define org_burningwave_jvm_NativeEnvironment_GENERATE_FIELD_ACCESSOR_DESTRUCTOR(jtype) \
	delete(this->jtype ## FieldAccessor); \
	this->jtype ## FieldAccessor = NULL;

#endif


NativeEnvironment::NativeEnvironment(JNIEnv* env) {
	this->init(env);
}


void NativeEnvironment::destroy(JNIEnv* jNIEnv){
	org_burningwave_jvm_NativeEnvironment_GENERATE_FIELD_ACCESSOR_DESTRUCTOR(jobject)
	org_burningwave_jvm_NativeEnvironment_GENERATE_FIELD_ACCESSOR_DESTRUCTOR(jshort)
	org_burningwave_jvm_NativeEnvironment_GENERATE_FIELD_ACCESSOR_DESTRUCTOR(jint)
	org_burningwave_jvm_NativeEnvironment_GENERATE_FIELD_ACCESSOR_DESTRUCTOR(jlong)
	org_burningwave_jvm_NativeEnvironment_GENERATE_FIELD_ACCESSOR_DESTRUCTOR(jfloat)
	org_burningwave_jvm_NativeEnvironment_GENERATE_FIELD_ACCESSOR_DESTRUCTOR(jdouble)
	org_burningwave_jvm_NativeEnvironment_GENERATE_FIELD_ACCESSOR_DESTRUCTOR(jboolean)
	org_burningwave_jvm_NativeEnvironment_GENERATE_FIELD_ACCESSOR_DESTRUCTOR(jbyte)
	org_burningwave_jvm_NativeEnvironment_GENERATE_FIELD_ACCESSOR_DESTRUCTOR(jchar)
}


void NativeEnvironment::init(JNIEnv* jNIEnv) {
	org_burningwave_jvm_NativeEnvironment_GENERATE_FIELD_ACCESSOR_INITIALIZATOR(Object, jobject)
	org_burningwave_jvm_NativeEnvironment_GENERATE_FIELD_ACCESSOR_INITIALIZATOR(Short, jshort)
	org_burningwave_jvm_NativeEnvironment_GENERATE_FIELD_ACCESSOR_INITIALIZATOR(Int, jint)
	org_burningwave_jvm_NativeEnvironment_GENERATE_FIELD_ACCESSOR_INITIALIZATOR(Long, jlong)
	org_burningwave_jvm_NativeEnvironment_GENERATE_FIELD_ACCESSOR_INITIALIZATOR(Float, jfloat)
	org_burningwave_jvm_NativeEnvironment_GENERATE_FIELD_ACCESSOR_INITIALIZATOR(Double, jdouble)
	org_burningwave_jvm_NativeEnvironment_GENERATE_FIELD_ACCESSOR_INITIALIZATOR(Boolean, jboolean)
	org_burningwave_jvm_NativeEnvironment_GENERATE_FIELD_ACCESSOR_INITIALIZATOR(Byte, jbyte)
	org_burningwave_jvm_NativeEnvironment_GENERATE_FIELD_ACCESSOR_INITIALIZATOR(Char, jchar)
}


template<typename Type>
FieldAccessor<Type>::FieldAccessor(
	Type (JNIEnv::*getFieldValueFunction) (jobject, jfieldID),
	Type (JNIEnv::*getStaticFieldValueFunction) (jclass, jfieldID),
	void (JNIEnv::*setValueFunction) (jobject, jfieldID, Type),
	void (JNIEnv::*setStaticValueFunction) (jclass , jfieldID, Type)
) {
	this->getFieldValueFunction = getFieldValueFunction;
	this->getStaticFieldValueFunction = getStaticFieldValueFunction;
	this->setValueFunction = setValueFunction;
	this->setStaticValueFunction = setStaticValueFunction;
}


template<typename Type>
FieldAccessor<Type>::~FieldAccessor() {}
