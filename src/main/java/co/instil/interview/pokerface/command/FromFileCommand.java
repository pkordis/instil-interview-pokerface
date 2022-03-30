package co.instil.interview.pokerface.command;

import co.instil.interview.pokerface.core.HandFileProcessor;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@RequiredArgsConstructor
@ShellComponent
public class FromFileCommand {
    @NonNull
    private final HandFileProcessor handFileProcessor;

    @ShellMethod("Reads the text file from the path provided, printing out the hand read \n"
      + "(if valid) with the hand name identified next to it. If there's something \n"
      + "wrong with the hand, the error identified will be printed next to the line \n"
      + "it was found at")
    public void fromFile(
      @ShellOption(
        help = "The path to the target file. It can be either absolute or relative"
      )
      final String filepath
    ) throws Exception {
        final Path sampleFilePath = Paths.get(filepath);
        handFileProcessor.processFile(sampleFilePath);
    }
}
