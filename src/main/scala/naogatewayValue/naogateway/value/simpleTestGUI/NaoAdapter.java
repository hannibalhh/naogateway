package naogateway.value.simpleTestGUI;

import org.zeromq.*;
import org.zeromq.ZMQ.*;
import static org.zeromq.ZMQ.*;


/** Testclass for the adapter
 * 
 */
public class NaoAdapter {
//    static {
//        System.loadLibrary("JNaoQi");
//    }
//    
    public static void main(String... args) throws Exception {        
        ZContext context = new ZContext();
        Socket socket = context.createSocket(REQ);
        socket.connect("tcp://127.0.0.1:5555");
        //Work here
        //Variant param = new Variant("Hallo Welt"); //Linker Error
        //HAWActorRPCRequest rpcReq = HAWActorRPCRequest.newBuilder().setModule("ALTextToSpeech").setMethod("say").addParams(convert(param)).build();
        //socket.send(rpcReq.toByteArray(),0);
        //socket.recv(0); //Throw received data away...
        
        /*Variant vArray = new Variant();
        Variant vString = new Variant("Hello");
        vArray.push_back(vString);
        Variant vString2 = vArray.getElement(0);
        vString2.getType(); //java.lang.IllegalArgumentException: No enum class com.aldebaran.proxy.Variant$typeV with value -1232044920
         */
        
        MainFrame frame = new MainFrame(socket);
        frame.show();
        
        //Example works =)
    }
}
