package naogateway.value.simpleTestGUI;

import naogateway.value.Hawactormsg;
import naogateway.value.Hawactormsg.MixedValue;
import naogateway.value.jnaoqi.Variant;

import com.google.protobuf.ByteString;

import static naogateway.value.jnaoqi.Variant.typeV.*;

//Konvertierungsklasse für Variant und Protobuf.MixedValue
public class JAdapter {
//    public static MixedValue convert(Variant variant) throws InvalidValueException {
//        MixedValue.Builder builder = MixedValue.newBuilder();
//        
//        variant.getType();
//        if (variant.getType() == VSTRING || variant.getType() == VCHARARRAY) //TODO ist mit VCHARARRAY wirklich char[] gemeint?
//            builder.setString(variant.toString());
//        else if (variant.getType() == VINT) 
//            builder.setInt(variant.toInt());
//        else if (variant.getType() == VBOOL)
//            builder.setBool(variant.toBoolean());
//        else if (variant.getType() == VFLOAT)
//            builder.setFloat(variant.toFloat());
//        else if (variant.getType() == VBINARY)
//            builder.setBinary(ByteString.copyFrom(variant.toBinary()));
//        else if (variant.getType() == VINTARRAY) {
//            int[] ary = variant.toIntArray();
//            for(int i : ary)
//                builder.addArray(MixedValue.newBuilder().setInt(i).build());
//        } else if (variant.getType() == VFLOATARRAY) {
//            float[] ary = variant.toFloatArray();
//            for(float f : ary)
//                builder.addArray(MixedValue.newBuilder().setFloat(f).build());
//        }  else if (variant.getType() == VARRAY) {
//            for (int i = 0; i < variant.getSize(); ++i)
//                builder.addArray(convert(variant.getElement(i)));
//        } else
//            throw new InvalidValueException("Encountered Variant with unknown type");
//        
//        return builder.build();
//    }
//    public static Variant convert(MixedValue mixed) throws InvalidValueException {
//        if (mixed.hasInt())
//            return new Variant(mixed.getInt());
//        if (mixed.hasFloat())
//            return new Variant(mixed.getFloat());
//        if (mixed.hasBool())
//            return new Variant(mixed.getBool());
//        if (mixed.hasString())
//            return new Variant(mixed.getString());
//        if (mixed.hasBinary())
//            return new Variant(mixed.getBinary().toByteArray());
//        if (mixed.getArrayCount() > 0) {
//            Variant v = new Variant();
//            for(MixedValue m : mixed.getArrayList())
//                v.push_back(convert(m));
//            return v;
//        }
//        throw new InvalidValueException("Encountered MixedValue with unknown type");
//    }
//    
//    public static String toString(Variant variant) {
//        if (variant.getType() == VSTRING || variant.getType() == VCHARARRAY) //TODO ist mit VCHARARRAY wirklich char[] gemeint?
//            return String.valueOf(variant.toString());
//        else if (variant.getType() == VINT) 
//            return String.valueOf(variant.toInt());
//        else if (variant.getType() == VBOOL)
//            return String.valueOf(variant.toBoolean());
//        else if (variant.getType() == VFLOAT)
//            return String.valueOf(variant.toFloat());
//        else if (variant.getType() == VBINARY)
//            return "Binary Data";
//        else if (variant.getType() == VINTARRAY)
//            return "Int-Array";
//        else if (variant.getType() == VFLOATARRAY)
//            return "Float-Array";
//        else if (variant.getType() == VARRAY)
//            return "Array";
//        else
//            return "unknown Variant type!";
//    }
    
    public static String toString(MixedValue mixed) {
        if (mixed.hasInt())
            return String.valueOf(mixed.getInt());
        else if (mixed.hasFloat())
            return String.valueOf(mixed.getFloat());
        else if (mixed.hasBool())
            return String.valueOf(mixed.getBool());
        else if (mixed.hasString())
            return mixed.getString();
        else if (mixed.hasBinary())
            return "Binary Data";
        else if (mixed.getArrayCount() > 0) {
            StringBuilder s = new StringBuilder();
            s.append("[");
            for(int i = 0;;) {
                s.append(JAdapter.toString(mixed.getArray(i++)));
                if (i < mixed.getArrayCount())
                    s.append(" ,");
                else 
                    break;
            }
            s.append(" ]");
            return s.toString();
        } else 
            return "Empty";
    }
    
}


class InvalidValueException extends Exception {
	private static final long serialVersionUID = -2405522202086533025L;
	public InvalidValueException(String message) {
        super(message);
    }
}