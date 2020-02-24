package es.rubencordero.FlyCloud;

import org.apache.commons.cli.*;

public class ParserController
{
    private CommandLineParser cmdParser;
    private HelpFormatter helpFormatter;
    private Options options;

    ParserController()
    {
        options = new Options();

        Option pushOption = new Option("push", true, "Push a file into FlyCloud");
        options.addOption(pushOption);

        Option pullOption = new Option("pull", true, "Pull a file from FlyCloud");
        options.addOption(pullOption);

        Option registerOption = new Option("register", false, "Create a new account in FlyCloud");
        options.addOption(registerOption);

        Option loginOption = new Option("login", false, "Log in your account in FlyCloud");
        options.addOption(loginOption);

        Option userOption = new Option("u","username", true, "Username");
        options.addOption(userOption);


        Option passwordOption = new Option("p", "password", true, "Password");
        options.addOption(passwordOption);

        cmdParser = new DefaultParser();
        helpFormatter = new HelpFormatter();

    }

    public CommandLineParser getCmdParser()
    {
        return cmdParser;
    }

    public HelpFormatter getHelpFormatter()
    {
        return helpFormatter;
    }

    public Options getOptions()
    {
        return options;
    }
}
