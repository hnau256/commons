package org.hnau.commons.ktlint

import com.pinterest.ktlint.cli.ruleset.core.api.RuleSetProviderV3
import com.pinterest.ktlint.rule.engine.core.api.RuleProvider
import com.pinterest.ktlint.rule.engine.core.api.RuleSetId

public class HnauRuleSetProvider :
    RuleSetProviderV3(
        RuleSetId("hnau"),
    ) {

    override fun getRuleProviders(): Set<RuleProvider> = setOf(
        RuleProvider { ArgumentListMultilineRule() },
    )
}
