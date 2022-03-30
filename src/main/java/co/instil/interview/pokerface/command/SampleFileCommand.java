package co.instil.interview.pokerface.command;

import co.instil.interview.pokerface.core.HandFileProcessor;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@RequiredArgsConstructor
@ShellComponent
public class SampleFileCommand {
    @NonNull
    private final HandFileProcessor handFileProcessor;

    @ShellMethod("Acts exactly as the 'from-file' command but uses a built-in sample file as input")
    public void sampleFile() throws Exception {
        final Path sampleFilePath = Paths.get(
          ClassLoader.getSystemResource("sampleFile.txt").toURI()
        );
        handFileProcessor.processFile(sampleFilePath);
    }
}
