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


NativeEnvironment::NativeEnvironment(JNIEnv* env) {
	this->init(env);
}


void NativeEnvironment::destroy(JNIEnv* jNIEnv){
	delete(this->jobjectFieldAccessor);
	this->jobjectFieldAccessor = NULL;

	this->jshortFieldAccessor->destroy(jNIEnv);
	delete(this->jshortFieldAccessor);
	this->jshortFieldAccessor = NULL;

	this->jintFieldAccessor->destroy(jNIEnv);
	delete(this->jintFieldAccessor);
	this->jintFieldAccessor = NULL;

	this->jlongFieldAccessor->destroy(jNIEnv);
	delete(this->jlongFieldAccessor);
	this->jlongFieldAccessor = NULL;

	this->jfloatFieldAccessor->destroy(jNIEnv);
	delete(this->jfloatFieldAccessor);
	this->jfloatFieldAccessor = NULL;

	this->jdoubleFieldAccessor->destroy(jNIEnv);
	delete(this->jdoubleFieldAccessor);
	this->jdoubleFieldAccessor = NULL;

	this->jbooleanFieldAccessor->destroy(jNIEnv);
	delete(this->jbooleanFieldAccessor);
	this->jbooleanFieldAccessor = NULL;

	this->jbyteFieldAccessor->destroy(jNIEnv);
	delete(this->jbyteFieldAccessor);
	this->jbyteFieldAccessor = NULL;

	this->jcharFieldAccessor->destroy(jNIEnv);
	delete(this->jcharFieldAccessor);
	this->jcharFieldAccessor = NULL;
}

void NativeEnvironment::init(JNIEnv* jNIEnv) {
	this->jobjectFieldAccessor = new FieldAccessor<jobject>(
		&JNIEnv::GetObjectField, &JNIEnv::GetStaticObjectField,
		&JNIEnv::SetObjectField, &JNIEnv::SetStaticObjectField
	);
	this->jshortFieldAccessor = new FieldAccessor<jshort>(
		&JNIEnv::GetShortField, &JNIEnv::GetStaticShortField,
		&JNIEnv::SetShortField, &JNIEnv::SetStaticShortField
	);
	this->jintFieldAccessor = new FieldAccessor<jint>(
		&JNIEnv::GetIntField, &JNIEnv::GetStaticIntField,
		&JNIEnv::SetIntField, &JNIEnv::SetStaticIntField
	);
	this->jlongFieldAccessor = new FieldAccessor<jlong>(
		&JNIEnv::GetLongField, &JNIEnv::GetStaticLongField,
		&JNIEnv::SetLongField, &JNIEnv::SetStaticLongField
	);
	this->jfloatFieldAccessor = new FieldAccessor<jfloat>(
		&JNIEnv::GetFloatField, &JNIEnv::GetStaticFloatField,
		&JNIEnv::SetFloatField, &JNIEnv::SetStaticFloatField
	);
	this->jdoubleFieldAccessor = new FieldAccessor<jdouble>(
		&JNIEnv::GetDoubleField, &JNIEnv::GetStaticDoubleField,
		&JNIEnv::SetDoubleField, &JNIEnv::SetStaticDoubleField
	);
	this->jbooleanFieldAccessor = new FieldAccessor<jboolean>(
		&JNIEnv::GetBooleanField, &JNIEnv::GetStaticBooleanField,
		&JNIEnv::SetBooleanField, &JNIEnv::SetStaticBooleanField
	);
	this->jbyteFieldAccessor = new FieldAccessor<jbyte>(
		&JNIEnv::GetByteField, &JNIEnv::GetStaticByteField,
		&JNIEnv::SetByteField, &JNIEnv::SetStaticByteField
	);
	this->jcharFieldAccessor = new FieldAccessor<jchar>(
		&JNIEnv::GetCharField, &JNIEnv::GetStaticCharField,
		&JNIEnv::SetCharField, &JNIEnv::SetStaticCharField
	);
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


template<typename Type>
void FieldAccessor<Type>::destroy(JNIEnv* jNIEnv) {}
