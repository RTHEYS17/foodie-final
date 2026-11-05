# Foodie

Foodie is a native Android restaurant menu app written in Kotlin. The chef can manage a menu of
about 100 dishes, and customers can add dishes to a cart and check out with a correct total.

## Features

**Menu**
- 100 dishes: 34 starters, 33 main courses and 33 desserts.
- Each dish has a name, description, course (Starter, Main Course or Dessert) and price.
- Add, edit and delete dishes. Changes show immediately.
- Search by dish name and filter by course. The Clear button resets both.
- Each dish card has a colour strip and badge showing its course.

**Cart and checkout**
- "Add to cart" on every dish. Adding the same dish again increases its quantity.
- The Cart tab shows a number badge with how many dishes are in the cart.
- Change quantities with + and -, remove a dish, or clear the whole cart (maximum 99 per dish).
- Each line shows the price per dish and a subtotal. The cart shows the number of items and the total.
- Checkout shows a receipt with every line and the total. "Place order" confirms it and empties the cart.

**Statistics**
- Total number of dishes, average price, number of dishes in each course,
  and the most and least expensive dish.

**User experience**
- Validation messages under each field on the add/edit form.
- Success and error messages at the bottom of the screen.
- Confirmation before deleting a dish and before placing an order.
- The Android back button returns to the Menu tab before closing the app.


## Project structure
All code is in `app/src/main/java/com/chefmenumanager`.

| File | Purpose |
|---|---|
| `MainActivity` | Header, bottom navigation, cart badge and messages |
| `Navigator` | Lets the screens ask the activity to change screen or show a message |
| `MenuFragment`, `MenuAdapter` | Menu list, search, course filter, edit, delete, add to cart |
| `FormFragment`, `MenuValidator` | Add/edit form and its validation rules |
| `CartFragment`, `CartAdapter` | Cart list, quantities, totals and checkout |
| `StatsFragment`, `MenuStats` | Statistics screen and the calculations behind it |
| `MenuRepository`, `SampleMenu` | The menu in memory and the 100 starting dishes |
| `CartRepository` | The cart: add, decrease, remove, clear, totals |
| `MenuItem`, `Courses` | Dish data class and the three courses with their colours |
| `Extensions` | Price formatting and the cents helpers |

Layouts, colours, strings and the theme are in `app/src/main/res`.

## How the cart total works
Prices are converted to cents (whole numbers) and multiplied by the quantity, then added up. This
avoids the rounding errors that decimal numbers can cause. For example, two dishes at R89.50 and one
at R70.00 give 2 x 8950 + 7000 = 24900 cents, shown as R249.00. The cart stores only each dish's id and
quantity, so if a dish's price is edited the cart uses the new price, and a dish that is deleted
disappears from the cart.

## Tests
- `MenuLogicTest`: form validation, price parsing and menu statistics.
- `CartTest`: 100 dishes loaded, correct total with several of the same dish, quantity changes,
  the 99 limit, exact cent formatting and removal of deleted dishes.

## Limitations
- Data is kept in memory only. The menu changes and the cart reset when the app is closed.
- The app is for one restaurant and has no login, payment or order history.
- Prices are in South African Rand (R).
