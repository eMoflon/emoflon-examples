package org.moflon.tie;

import java.util.Collection;

import org.moflon.tgg.algorithm.configuration.Configurator;
import org.moflon.tgg.algorithm.configuration.RuleResult;

public class AuthorConfigurator implements Configurator {
	@Override
	public RuleResult chooseOne(Collection<RuleResult> alternatives) {
		return alternatives.stream()
				.filter(rr -> rr.isRule("ExistingAuthorRule")).findAny()
				.orElse(Configurator.super.chooseOne(alternatives));
	}

}
