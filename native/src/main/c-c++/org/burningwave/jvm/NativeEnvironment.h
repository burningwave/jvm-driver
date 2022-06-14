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

#ifndef org_burningwave_jvm_NativeEnvironment_H
	#define org_burningwave_jvm_NativeEnvironment_H

	template<typename Type>
	class FieldAccessor {

		public:
			FieldAccessor(
				Type (JNIEnv::*getFieldValueFunction) (jobject, jfieldID),
				Type (JNIEnv::*getStaticFieldValueFunction) (jclass, jfieldID),
				void (JNIEnv::*setValueFunction) (jobject, jfieldID, Type),
				void (JNIEnv::*setStaticValueFunction) (jclass , jfieldID, Type)
			);

			~FieldAccessor();

			Type getValue(JNIEnv* env, jobject target, jobject field);

			Type getStaticValue(JNIEnv* env, jclass target, jobject field);

			void setValue(JNIEnv* env, jobject target, jobject field, Type value);

			void setStaticValue(JNIEnv* env, jclass target, jobject field, Type value);

		private:
			Type (JNIEnv::*getFieldValueFunction) (jobject, jfieldID);
			Type (JNIEnv::*getStaticFieldValueFunction) (jclass, jfieldID);
			void (JNIEnv::*setValueFunction) (jobject, jfieldID, Type);
			void (JNIEnv::*setStaticValueFunction) (jclass, jfieldID, Type);

	};
	
	class NativeEnvironment {
		public:
			NativeEnvironment(JNIEnv* env);

			void destroy(JNIEnv* env);

		public:
			FieldAccessor<jobject>* jobjectFieldAccessor;
			FieldAccessor<jshort>* jshortFieldAccessor;
			FieldAccessor<jint>* jintFieldAccessor;
			FieldAccessor<jlong>* jlongFieldAccessor;
			FieldAccessor<jfloat>* jfloatFieldAccessor;
			FieldAccessor<jdouble>* jdoubleFieldAccessor;
			FieldAccessor<jboolean>* jbooleanFieldAccessor;
			FieldAccessor<jbyte>* jbyteFieldAccessor;
			FieldAccessor<jchar>* jcharFieldAccessor;

			void init(JNIEnv*);
	};

	
	template<typename Type>
	Type FieldAccessor<Type>::getValue(JNIEnv* jNIEnv, jobject target, jobject field) {
		return (jNIEnv->*getFieldValueFunction)(target, jNIEnv->FromReflectedField(field));
	}


	template<typename Type>
	Type FieldAccessor<Type>::getStaticValue(JNIEnv* jNIEnv, jclass target, jobject field) {
		return (jNIEnv->*getStaticFieldValueFunction)(target, jNIEnv->FromReflectedField(field));
	}


	template<typename Type>
	void FieldAccessor<Type>::setValue(JNIEnv* jNIEnv, jobject target, jobject field, Type value) {
		(jNIEnv->*setValueFunction)(target, jNIEnv->FromReflectedField(field), value);
	}


	template<typename Type>
	void FieldAccessor<Type>::setStaticValue(JNIEnv* jNIEnv, jclass target, jobject field, Type value) {
		(jNIEnv->*setStaticValueFunction)(target, jNIEnv->FromReflectedField(field), value);
	}

#endif
