package org.hnau.commons.app.test.app

import org.hnau.commons.gen.pipe.annotations.Pipe
import org.hnau.commons.app.test.data.Currency

@Pipe
interface CommonsAppTestAppDependencies {

    val currency: Currency

    companion object
}