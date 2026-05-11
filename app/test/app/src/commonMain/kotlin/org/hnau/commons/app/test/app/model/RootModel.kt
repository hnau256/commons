package org.hnau.commons.app.test.app.model

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import org.hnau.commons.app.model.goback.GoBackHandler
import org.hnau.commons.app.model.goback.NeverGoBackHandler
import org.hnau.commons.gen.pipe.annotations.Pipe
import org.hnau.commons.kotlin.coroutines.actionOrCancelIfExecuting
import kotlin.time.Duration.Companion.seconds

class RootModel(
    scope: CoroutineScope,
    dependencies: Dependencies,
    skeleton: Skeleton,
) {

    @Pipe
    interface Dependencies {

        companion object
    }

    @Serializable
    data class Skeleton(
        val form: FormModel.Skeleton = FormModel.Skeleton(
            initial = Config(
                flag = true,
                decimal = BigDecimal.fromFloat(123.456f),
                integer = BigInteger.fromInt(123),
                text = "qwerty",
            )
        ),
    )

    val form = FormModel(
        scope = scope,
        skeleton = skeleton.form,
    )

    val goBackHandler: GoBackHandler
        get() = NeverGoBackHandler
}