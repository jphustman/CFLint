package com.cflint;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import cfml.parsing.reporting.ParseException;

import com.cflint.config.CFLintPluginInfo.PluginInfoRule;
import com.cflint.config.CFLintPluginInfo.PluginInfoRule.PluginMessage;
import com.cflint.config.ConfigRuntime;
import com.cflint.plugins.core.BooleanExpressionChecker;

public class TestBooleanExpressionChecker {

	private CFLint cfBugs;

	@Before
	public void setUp() {
		final ConfigRuntime conf = new ConfigRuntime();
		final PluginInfoRule pluginRule = new PluginInfoRule();
		pluginRule.setName("BooleanExpressionChecker");
		conf.getRules().add(pluginRule);
		final PluginMessage pluginMessage = new PluginMessage("EXPLICIT_BOOLEAN_CHECK");
		pluginMessage.setSeverity("INFO");
		cfBugs = new CFLint(conf, new BooleanExpressionChecker());
	}

	@Test
	public void testBooleanExpressionInScript() throws ParseException, IOException {
		final String scriptSrc = "<cfscript>\r\n"
			+ "if (a && b == true) {\r\n"
			+ "	c = 1;\r\n"
			+ "}\r\n"
			+ "else if (a or b is false) {\r\n"
			+ "	c = 1;\r\n"
			+ "}\r\n"
			+ "</cfscript>";
			
		cfBugs.process(scriptSrc, "test");
		final List<BugInfo> result = cfBugs.getBugs().getBugList().values().iterator().next();
		assertEquals(2, result.size());
		assertEquals("EXPLICIT_BOOLEAN_CHECK", result.get(0).getMessageCode());
		assertEquals(2, result.get(0).getLine());
		assertEquals("EXPLICIT_BOOLEAN_CHECK", result.get(1).getMessageCode());
		assertEquals(5, result.get(1).getLine());
	}

	@Test
	public void testBooleanExpressionInTag() throws ParseException, IOException {
		final String tagSrc = "<cfset a = 23>\r\n"
			+ "<cfset a = not (b and c) is false>";
			
		cfBugs.process(tagSrc, "test");
		final List<BugInfo> result = cfBugs.getBugs().getBugList().values().iterator().next();
		assertEquals(1, result.size());
		assertEquals("EXPLICIT_BOOLEAN_CHECK", result.get(0).getMessageCode());
		assertEquals(2, result.get(0).getLine());
	}

}
