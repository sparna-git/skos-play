package fr.sparna.cli;

import java.util.Arrays;
import java.util.List;

import com.beust.jcommander.converters.IParameterSplitter;

public class SpaceSplitter implements IParameterSplitter {

	@Override
	public List<String> split(String param) {
		return Arrays.asList(param.split(" "));
	}

}
