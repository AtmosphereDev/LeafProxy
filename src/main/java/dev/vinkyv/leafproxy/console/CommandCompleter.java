package dev.vinkyv.leafproxy.console;

import dev.vinkyv.leafproxy.LeafServer;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.List;

public class CommandCompleter implements Completer {
	private final LeafServer proxy;

	public CommandCompleter(LeafServer proxy) {
		this.proxy = proxy;
	}

	@Override
	public void complete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> candidates) {
		candidates.add(new Candidate("stop"));
	}
}
