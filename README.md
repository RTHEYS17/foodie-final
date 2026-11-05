# Foodie (Kotlin, Android)

Native Android app written in Kotlin (Views, ViewBinding, Material 3). Nothing else to install.

## Run it
1. Unzip.
2. Android Studio > File > Open, and choose the `Foodie` folder (the one containing `gradlew`).
3. Wait for "Gradle sync" to finish (first time takes a few minutes). Accept any prompt to install
   Android SDK Platform 34. Do not upgrade AGP/Gradle if asked.
4. Device Manager > pick or create a virtual phone > start it.
5. Press the green Run button.

Optional: right-click `app/src/test` > Run Tests (validation, statistics and cart total tests).

## What it does
- Menu: 100 dishes (34 starters, 33 mains, 33 desserts), search by name, filter by course, add/edit/delete.
- Cart: add dishes from the menu, change quantities, remove, clear. The Cart tab shows a number badge.
- Checkout: receipt with every line and the total, then "Place order" empties the cart.
- Statistics: total dishes, average price, dishes per course, most and least expensive.

## Code map (app/src/main/java/com/chefmenumanager)
| Area | Files |
|---|---|
| Menu data (100 dishes) | SampleMenu, MenuRepository, MenuItem, Courses |
| Menu screen | MenuFragment, MenuAdapter |
| Add / edit form and validation | FormFragment, MenuValidator |
| Cart and checkout (totals in cents, so they are exact) | CartRepository, CartFragment, CartAdapter, Extensions |
| Statistics | StatsFragment, MenuStats |
| Navigation and messages | MainActivity, Navigator |
