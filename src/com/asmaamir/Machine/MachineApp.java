package com.asmaamir.Machine;

public class MachineApp {


    public static void main(String[] args) throws Exception {

        //MCForm f = new MCForm();
        MachineClient client = new MachineClient();
        client.sendMessages();
    }


}
