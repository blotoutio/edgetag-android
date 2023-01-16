# Defined Events

## mapID
The `mapID` method allows you to map external services to Blotout ID.

#### Input
`mapID(mapIDData: MapIDData, withInformation: HashMap<String, Any>?)`

|||||
|---|---|---|---|
| `mapIDData` | `MapIDData` | Required | See data table. |
| `eventInfo` | `Object` | Optional | You can provide some additional data to this event. There is no limitation as this is just a key-value pair send to the server. |

#### MapIDData
|              |          |          |                                                            |
| ------------ | -------- | -------- | ---------------------------------------------------------- |
| `externalID` | `String` | Required | External ID that you want to link to Blotout ID.           |
| `provider`   | `String` | Required | Provider that generated external ID, for example `hubspot` |


#### Example

```kotlin
HashMap<String,Any> eventInfo = new HashMap<>()
eventInfo.put("language","en")

var data = MapIDData()
data.externalID = "92j2jr230r-232j9j2342j3-jiji"
data.provider = "sass"

BlotoutAnalytics.INSTANCE.mapID(data, null)
BlotoutAnalytics.INSTANCE.mapID(data, eventInfo)
```

## transaction

The `transaction` method allows you to record tranasctions in your system, like purchase in ecommerce.

#### Input

|                  |          |          |                                                                                                                                 |
| ---------------- | -------- | -------- | ------------------------------------------------------------------------------------------------------------------------------- |
| `transactionData`      | `TransactionData` | Required | See data table.                                                                                                                 |
| `additionalData` | `HashMap` | Optional | You can provide some additional data to this event. There is no limitation as this is just a key-value pair send to the server. |
                                                                                         |

#### Data

|              |          |          |                                                            |
| ------------ | -------- | -------- | ---------------------------------------------------------- |
| `transaction_id` | `String` | Required | Transaction ID.           |
| `transaction_currency`   | `String` | Optional | Currency used for the transaction. Example: `EUR` |
| `transaction_payment`   | `String` | Optional | Payment type used in the transaction. Example: `credit-card` |
| `transaction_total`   | `Double` | Optional | Total amount for the transaction. Example `10.50` |
| `transaction_discount`   | `Double` | Optional | Discount that was applied in the transaction. Example: `2.1` |
| `transaction_shipping`   | `Double` | Optional | Shipping that was charged in the transaction. Example: `5.0` |
| `transaction_tax`   | `Double` | Optional | How much tax was applied in the transaction. Example: `1.21` |

#### Example

{% tabs basic %}
{% tab basic kotlin %}

```kotlin
val transactionData = TransactionData(transaction_id = "123423423",transaction_currency = "$",transaction_payment = "1234",transaction_total = 1234.00,
                                    transaction_discount = 12,transaction_shipping = null,transaction_tax = 10.25)
        val withInformation = hashMapOf<String, Any>()
        withInformation.put("Language","EN")
        BlotoutAnalytics.transaction(transactionData, withInformation)

```

{% endtab %}
{% tab basic Java %}

```Java
TransactionData transactionData = new TransactionData("123423423",
                                    "$","1234",1234.00,
                                12,null,10.25);
BlotoutAnalytics.INSTANCE.transaction(transactionData,null);
```

{% endtab %}
{% endtabs %}

## item

The `item` method allows you to record item in your system, like add to cart in ecommerce.

#### Input

|                  |          |          |                                                                                                                                 |
| ---------------- | -------- | -------- | ------------------------------------------------------------------------------------------------------------------------------- |
| `data`      | `Item` | Required | See data table.                                                                                                                 |
| `additionalData` | `HashMap` | Optional | You can provide some additional data to this event. There is no limitation as this is just a key-value pair send to the server. |

#### Data

|              |          |          |                                                            |
| ------------ | -------- | -------- | ---------------------------------------------------------- |
| `item_id` | `String` | Required | Item ID.           |
| `item_name`   | `String` | Optional | Example: `Phone 4` |
| `item_sku`   | `String` | Optional | Example: `SHOP-01` |
| `item_category`   | `Array` | Optional | Example `['mobile', 'free-time]` |
| `item_currency`   | `String` | Optional | Currency of item price. Example: `EUR` |
| `item_price`   | `Double` | Optional | Example: `2.1` |
| `quantity`   | `Double` | Optional | Example: `3` |

#### Example

{% tabs basic %}
{% tab basic kotlin %}

```kotlin
val itemData = Item(item_id= "123423423", item_currency= "EUR", item_price= 10.5, quantity= 2)
BlotoutAnalytics.item(itemData,null)

```

{% endtab %}
{% tab basic java %}

```Java

Item itemData = new Item( "123423423",null,1,null, "EUR",  10.5,  2);
BlotoutAnalytics.INSTANCE.item(itemData,null);
```

{% endtab %}
{% endtabs %}

## persona

The `persona` method allows you to record persona in your system, like when user signs up or saves user profile.

#### Input

|                  |          |          |                                                                                                                                 |
| ---------------- | -------- | -------- | ------------------------------------------------------------------------------------------------------------------------------- |
| `data`      | `Object` | Required | See data table.                                                                                                                 |
| `additionalData` | `HashMap` | Optional | You can provide some additional data to this event. There is no limitation as this is just a key-value pair send to the server. |

#### Data

|              |          |          |                                                            |
| ------------ | -------- | -------- | ---------------------------------------------------------- |
| `persona_id` | `String` | Required | Persona ID.           |
| `persona_firstname`   | `String` | Optional | Example: `John` |
| `persona_lastname`   | `String` | Optional | Example: `Smith` |
| `persona_middlename`   | `String` | Optional | Example `Jack` |
| `persona_username`   | `String` | Optional | Example: `jsmith` |
| `persona_dob`   | `String` | Optional | Date of birth. Example: `04/30/2000` |
| `persona_email`   | `String` | Optional | Example: `john@domain.com` |
| `persona_number`   | `String` | Optional | Example: `+386 31 777 444` |
| `persona_address`   | `String` | Optional | Example: `Street 1` |
| `persona_city`   | `String` | Optional | Example: `San Francisco` |
| `persona_state`   | `String` | Optional | Example: `CA` |
| `persona_zip`   | `Double` | Optional | Example: `10000` |
| `persona_country`   | `String` | Optional | Example: `US` |
| `persona_gender`   | `String` | Optional | Example: `Female` |
| `persona_age`   | `Double` | Optional | Example: `22` |

#### Example

{% tabs basic %}
{% tab basic kotlin %}

```kotlin
val persona_item = Persona(persona_id = "3434343", persona_gender = "female", persona_age = 22)
BlotoutAnalytics.persona(persona_item, null)
```

{% endtab %}
{% tab basic java %}

```java

Persona persona_item = new Persona({ "3434343",null,null,null,null,null,null,null,null,null,null,null,null, gender: 'female', age: 22 })
BlotoutAnalytics.INSTANCE.persona(persona_item,null);
```

{% endtab %}
{% endtabs %}
