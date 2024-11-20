function(
    name = "getBills",
    description = "Retrieve bill information of a user",
    params = types(
        string(
            name = "userId",
            description = "UserId to be used to fetch bill"
        )
    ),
) { (userId) ->
    val customerBills = get<CustomerBills>()
    return customerBills.bills.joinToString(separator = "\n") { bill ->
        "your bill for ${bill.productName} is ${bill.amount}"
    }