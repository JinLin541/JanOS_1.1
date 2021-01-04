package util;

import memory.register.R1;
import memory.register.R2;
import memory.register.Register;

public class Utils {
    public static Register lookingRegisterForString(String RegisterName){
        Register r = null;
        switch (RegisterName){
            case "R1":
                r = R1.getR1();
                break;
            case "R2":
                r = R2.getR2();
                break;
            default:
                break;
        }
        return r;
    }
}
