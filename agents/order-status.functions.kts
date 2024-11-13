// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

function(
    name = "get_order_status",
    description = "Get the order status of an order.",
    params =
    types(
        string(
            name = "orderId",
            description = "OrderId of the customer order"
        )
    ),
) { (orderId) ->
    when(orderId) {
        "112233" -> "Your order is in prgress and will be delivered to you in next 2 days."
        "440000" -> "Your order shipment is not created yet."
        "200000" -> "Your order was delivered on 10 November 2024"
        "300000" -> "Your order was delivered on 5 November 2024"
        else -> """You can track your order by visiting this url: "https://telekom.de/orders" """"
    }
}