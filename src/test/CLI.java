package test;

import test.Commands.Command;
import test.Commands.DefaultIO;
import test.Commands.exitCommand;

import java.util.ArrayList;

public class CLI {

    ArrayList<Command> commands;
    DefaultIO dio;
    Commands c;

    public CLI(DefaultIO dio) {
        this.dio=dio;
        c=new Commands(dio);
        commands=new ArrayList<>();

        //Adding the relevant commands to the CLI
        commands.add(c.new UploadCommand());
        commands.add(c.new AlgorithmSettingsCommand());
        commands.add(c.new AnomalyDetectionCommand());
        commands.add(c.new DisplayResultsCommand());
        commands.add(c.new UploadAnomalisAndAnalyzeCommand());
        commands.add(c.new exitCommand());

    }

    public void start() {
        printCommands();
        String c= dio.readText();
        int ch = Integer.parseInt(c);
        while(!commands.get(ch-1).getClass().equals(exitCommand.class)) {
            commands.get(ch-1).execute();
            printCommands();
            c= dio.readText();
            ch = Integer.parseInt(c);
        }

    }

    public void printCommands() {
        int i=0;
        dio.write("Welcome to the Anomaly Detection Server.\n");
        dio.write("Please choose an option:\n");
        for(Command c : commands) {
            dio.write(i+1 + ". " + c+"\n");
            i++;
        }
    }
}