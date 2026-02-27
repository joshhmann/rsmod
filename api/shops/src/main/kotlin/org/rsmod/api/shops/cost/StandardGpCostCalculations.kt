package org.rsmod.api.shops.cost

import kotlin.math.floor
import kotlin.math.max

public object StandardGpCostCalculations {
    public const val BUY_FROM_SHOP_MIN_FRACTION: Double = 0.3
    public const val SELL_TO_SHOP_MIN_FRACTION: Double = 0.1

    public data class PriceParameters(
        val basePriceWithMarkup: Int,
        val stockDifference: Int,
        val firstObjPrice: Int,
        val priceChangePerObj: Int,
    )

    public data class BulkPriceParameters(
        val count: Int,
        val totalValue: Int,
        val firstObjPrice: Int,
    )

    public fun calculatePriceParameters(
        initialStock: Int,
        currentStock: Int,
        baseCost: Int,
        exchangePercentage: Double,
        changePercentage: Double,
        cap: Double,
        minCost: Int,
    ): PriceParameters {
        val stockDifference = initialStock.toLong() - currentStock.toLong()
        val difference = stockDifference * baseCost * (changePercentage / 100.0)
        val basePriceWithMarkup = baseCost * (exchangePercentage / 100.0)
        val rawFirstObjPrice = floor(basePriceWithMarkup + difference)
        val minPrice = (baseCost * cap).coerceAtLeast(minCost.toDouble())
        val firstObjPrice = max(minPrice, rawFirstObjPrice).toInt()

        val priceChangePerObj = max(minCost, (baseCost * (changePercentage / 100.0)).toInt())
        return PriceParameters(
            basePriceWithMarkup = basePriceWithMarkup.toInt(),
            stockDifference = stockDifference.toInt(),
            firstObjPrice = firstObjPrice,
            priceChangePerObj = priceChangePerObj,
        )
    }

    /* Logic for buying objs from shop */

    public fun calculateShopSellSingleValue(
        initialStock: Int,
        currentStock: Int,
        baseCost: Int,
        sellPercentage: Double,
        changePercentage: Double,
    ): Int {
        val parameters =
            calculatePriceParameters(
                initialStock = initialStock,
                currentStock = currentStock,
                baseCost = baseCost,
                exchangePercentage = sellPercentage,
                changePercentage = changePercentage,
                cap = BUY_FROM_SHOP_MIN_FRACTION,
                minCost = 1,
            )
        return parameters.firstObjPrice
    }

    public fun calculateShopSellBulkParameters(
        initialStock: Int,
        currentStock: Int,
        baseCost: Int,
        requestedCount: Int,
        availableCurrency: Int,
        sellPercentage: Double,
        changePercentage: Double,
    ): BulkPriceParameters {
        val firstObjPrice =
            calculateShopSellSingleValue(
                initialStock = initialStock,
                currentStock = currentStock,
                baseCost = baseCost,
                sellPercentage = sellPercentage,
                changePercentage = changePercentage,
            )
        val (exchangeCount, totalValue) =
            calculateSellAccumulation(
                initialStock = initialStock,
                currentStock = currentStock,
                baseCost = baseCost,
                firstObjPrice = firstObjPrice,
                requestedCount = requestedCount,
                availableCurrency = availableCurrency,
                sellPercentage = sellPercentage,
                changePercentage = changePercentage,
            )

        return BulkPriceParameters(
            count = exchangeCount,
            totalValue = totalValue,
            firstObjPrice = firstObjPrice,
        )
    }

    private tailrec fun calculateSellAccumulation(
        initialStock: Int,
        currentStock: Int,
        baseCost: Int,
        firstObjPrice: Int,
        requestedCount: Int,
        availableCurrency: Int,
        sellPercentage: Double,
        changePercentage: Double,
        exchangeCount: Int = 0,
        totalValue: Int = 0,
        currentCost: Int = firstObjPrice,
    ): Pair<Int, Int> {
        if (exchangeCount >= requestedCount || totalValue + currentCost > availableCurrency) {
            return exchangeCount to totalValue
        }

        val nextCost =
            calculateShopSellSingleValue(
                initialStock = initialStock,
                currentStock = currentStock - exchangeCount - 1,
                baseCost = baseCost,
                sellPercentage = sellPercentage,
                changePercentage = changePercentage,
            )

        return calculateSellAccumulation(
            initialStock = initialStock,
            currentStock = currentStock,
            baseCost = baseCost,
            firstObjPrice = firstObjPrice,
            requestedCount = requestedCount,
            availableCurrency = availableCurrency,
            sellPercentage = sellPercentage,
            changePercentage = changePercentage,
            exchangeCount = exchangeCount + 1,
            totalValue = totalValue + currentCost,
            currentCost = nextCost,
        )
    }

    /* Logic for selling objs to shop */

    public fun calculateShopBuySingleValue(
        initialStock: Int,
        currentStock: Int,
        baseCost: Int,
        buyPercentage: Double,
        changePercentage: Double,
    ): Int {
        val parameters =
            calculatePriceParameters(
                initialStock = initialStock,
                currentStock = currentStock,
                baseCost = baseCost,
                exchangePercentage = buyPercentage,
                changePercentage = changePercentage,
                cap = SELL_TO_SHOP_MIN_FRACTION,
                minCost = 0,
            )
        return parameters.firstObjPrice
    }

    public fun calculateShopBuyBulkParameters(
        initialStock: Int,
        currentStock: Int,
        baseCost: Int,
        requestedCount: Int,
        currencyCap: Int,
        buyPercentage: Double,
        changePercentage: Double,
    ): BulkPriceParameters {
        val firstObjPrice =
            calculateShopBuySingleValue(
                initialStock = initialStock,
                currentStock = currentStock,
                baseCost = baseCost,
                buyPercentage = buyPercentage,
                changePercentage = changePercentage,
            )
        val (exchangeCount, totalValue) =
            calculateBuyAccumulation(
                initialStock = initialStock,
                currentStock = currentStock,
                baseCost = baseCost,
                firstObjPrice = firstObjPrice,
                requestedCount = requestedCount,
                currencyCap = currencyCap,
                buyPercentage = buyPercentage,
                changePercentage = changePercentage,
            )

        return BulkPriceParameters(
            count = exchangeCount,
            totalValue = totalValue,
            firstObjPrice = firstObjPrice,
        )
    }

    private tailrec fun calculateBuyAccumulation(
        initialStock: Int,
        currentStock: Int,
        baseCost: Int,
        firstObjPrice: Int,
        requestedCount: Int,
        currencyCap: Int,
        buyPercentage: Double,
        changePercentage: Double,
        exchangeCount: Int = 0,
        totalValue: Int = 0,
        currentPrice: Int = firstObjPrice,
    ): Pair<Int, Int> {
        if (exchangeCount >= requestedCount || totalValue + currentPrice > currencyCap) {
            return exchangeCount to totalValue
        }

        val nextPrice =
            calculateShopBuySingleValue(
                initialStock = initialStock,
                currentStock = currentStock + exchangeCount + 1,
                baseCost = baseCost,
                buyPercentage = buyPercentage,
                changePercentage = changePercentage,
            )

        return calculateBuyAccumulation(
            initialStock = initialStock,
            currentStock = currentStock,
            baseCost = baseCost,
            firstObjPrice = firstObjPrice,
            requestedCount = requestedCount,
            currencyCap = currencyCap,
            buyPercentage = buyPercentage,
            changePercentage = changePercentage,
            exchangeCount = exchangeCount + 1,
            totalValue = totalValue + currentPrice,
            currentPrice = nextPrice,
        )
    }
}
