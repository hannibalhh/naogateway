/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.0
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package naogateway.value.jnaoqi;

final public class Variant {
  private long swigCPtr;
//  protected boolean swigCMemOwn;
//
//  private Variant(long cPtr, boolean cMemoryOwn) {
//    swigCMemOwn = cMemoryOwn;
//    swigCPtr = cPtr;
//  }

  public static long getCPtr(Variant obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

//  protected void finalize() {
//    delete();
//  }

//  public synchronized void delete() {
//    if (swigCPtr != 0) {
//      if (swigCMemOwn) {
//        swigCMemOwn = false;
//        JNaoQiJNI.delete_Variant(swigCPtr);
//      }
//      swigCPtr = 0;
//    }
//  }

//  public void setVariantType(Variant.typeV value) {
//    JNaoQiJNI.Variant_variantType_set(swigCPtr, this, value.swigValue());
//  }
//
//  public Variant.typeV getVariantType() {
//    return Variant.typeV.swigToEnum(JNaoQiJNI.Variant_variantType_get(swigCPtr, this));
//  }
//
//  public Variant() {
//    this(JNaoQiJNI.new_Variant__SWIG_0(), true);
//  }
//
//  public Variant(int a) {
//    this(JNaoQiJNI.new_Variant__SWIG_1(a), true);
//  }
//
//  public Variant(float f) {
//    this(JNaoQiJNI.new_Variant__SWIG_2(f), true);
//  }
//
//  public Variant(Variant s) {
//    this(JNaoQiJNI.new_Variant__SWIG_3(Variant.getCPtr(s), s), true);
//  }
//
//  public Variant(String s) {
//    this(JNaoQiJNI.new_Variant__SWIG_4(s), true);
//  }
//
//  public Variant.typeV getType() {
//    return Variant.typeV.swigToEnum(JNaoQiJNI.Variant_getType(swigCPtr, this));
//  }
//
//  public SWIGTYPE_p_AL__ALValue getValue() {
//    long cPtr = JNaoQiJNI.Variant_getValue(swigCPtr, this);
//    return (cPtr == 0) ? null : new SWIGTYPE_p_AL__ALValue(cPtr, false);
//  }

//  public Variant(byte[] b) {
//    this(JNaoQiJNI.new_Variant__SWIG_5(b), true);
//  }
//
//  public Variant(boolean b) {
//    this(JNaoQiJNI.new_Variant__SWIG_6(b), true);
//  }
//
//  public Variant(String[] as) {
//    this(JNaoQiJNI.new_Variant__SWIG_7(as), true);
//  }
//
//  public Variant(float[] af) {
//    this(JNaoQiJNI.new_Variant__SWIG_8(af), true);
//  }
//
//  public Variant(int[] ai) {
//    this(JNaoQiJNI.new_Variant__SWIG_9(ai), true);
//  }

//  public int toInt() {
//    return JNaoQiJNI.Variant_toInt(swigCPtr, this);
//  }
//
//  public String toString() {
//    return JNaoQiJNI.Variant_toString(swigCPtr, this);
//  }
//
//  public float toFloat() {
//    return JNaoQiJNI.Variant_toFloat(swigCPtr, this);
//  }
//
//  public boolean toBoolean() {
//    return JNaoQiJNI.Variant_toBoolean(swigCPtr, this);
//  }
//
//  public void push_back(Variant v) {
//    JNaoQiJNI.Variant_push_back(swigCPtr, this, Variant.getCPtr(v), v);
//  }
//
//  public Variant getElement(int i) {
//    return new Variant(JNaoQiJNI.Variant_getElement(swigCPtr, this, i), true);
//  }
//
//  public byte[] toBinary() {
//    return JNaoQiJNI.Variant_toBinary(swigCPtr, this);
//  }
//
//  public float[] toFloatArray() {
//    return JNaoQiJNI.Variant_toFloatArray(swigCPtr, this);
//  }
//
//  public int[] toIntArray() {
//    return JNaoQiJNI.Variant_toIntArray(swigCPtr, this);
//  }
//
//  public Object[] toStringArray() {
//    return JNaoQiJNI.Variant_toStringArray(swigCPtr, this);
//  }
//
//  public int getSize() {
//    return JNaoQiJNI.Variant_getSize(swigCPtr, this);
//  }
//
//  public String getBuffer() {
//    return JNaoQiJNI.Variant_getBuffer(swigCPtr, this);
//  }

//  public SWIGTYPE_p_AL__ALValue toALValue() {
//    return new SWIGTYPE_p_AL__ALValue(JNaoQiJNI.Variant_toALValue(swigCPtr, this), true);
//  }
//
//  public void fromALValue(SWIGTYPE_p_AL__ALValue val) {
//    JNaoQiJNI.Variant_fromALValue(swigCPtr, this, SWIGTYPE_p_AL__ALValue.getCPtr(val));
//  }

  public final static class typeV {
    public final static typeV VINT = new typeV("VINT", JNaoQiJNI.Variant_VINT_get());
    public final static typeV VBOOL = new typeV("VBOOL", JNaoQiJNI.Variant_VBOOL_get());
    public final static typeV VSTRING = new typeV("VSTRING", JNaoQiJNI.Variant_VSTRING_get());
    public final static typeV VCHARARRAY = new typeV("VCHARARRAY", JNaoQiJNI.Variant_VCHARARRAY_get());
    public final static typeV VFLOATARRAY = new typeV("VFLOATARRAY", JNaoQiJNI.Variant_VFLOATARRAY_get());
    public final static typeV VARRAY = new typeV("VARRAY", JNaoQiJNI.Variant_VARRAY_get());
    public final static typeV VINTARRAY = new typeV("VINTARRAY", JNaoQiJNI.Variant_VINTARRAY_get());
    public final static typeV VFLOAT = new typeV("VFLOAT", JNaoQiJNI.Variant_VFLOAT_get());
    public final static typeV VBINARY = new typeV("VBINARY", JNaoQiJNI.Variant_VBINARY_get());

//    public final int swigValue() {
//      return swigValue;
//    }

    public String toString() {
      return swigName;
    }

//    public static typeV swigToEnum(int swigValue) {
//      if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
//        return swigValues[swigValue];
//      for (int i = 0; i < swigValues.length; i++)
//        if (swigValues[i].swigValue == swigValue)
//          return swigValues[i];
//      throw new IllegalArgumentException("No enum " + typeV.class + " with value " + swigValue);
//    }

//    private typeV(String swigName) {
//      this.swigName = swigName;
//      this.swigValue = swigNext++;
//    }

    private typeV(String swigName, int swigValue) {
      this.swigName = swigName;
      this.swigValue = swigValue;
      swigNext = swigValue+1;
    }

//    private typeV(String swigName, typeV swigEnum) {
//      this.swigName = swigName;
//      this.swigValue = swigEnum.swigValue;
//      swigNext = this.swigValue+1;
//    }

    private static typeV[] swigValues = { VINT, VBOOL, VSTRING, VCHARARRAY, VFLOATARRAY, VARRAY, VINTARRAY, VFLOAT, VBINARY };
    private static int swigNext = 0;
    private final int swigValue;
    private final String swigName;
  }

}