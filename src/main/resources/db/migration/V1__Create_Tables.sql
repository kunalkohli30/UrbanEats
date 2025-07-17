DROP TABLE IF EXISTS `address`;
CREATE TABLE `address` (
  `id` bigint NOT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `street_address` varchar(255) DEFAULT NULL,
  `zip_code` varchar(255) DEFAULT NULL,
  `area_name` varchar(255) DEFAULT NULL,
  `address_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart` (
  `customer_id` varchar(255) DEFAULT NULL,
  `id` bigint NOT NULL,
  `total` int DEFAULT NULL,
  `restaurant_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK867x3yysb1f3jk41cv3vsoejj` (`customer_id`),
  KEY `FKd8jnj2kfxpcyapdle9aoqe02q` (`restaurant_id`),
  CONSTRAINT `FKd8jnj2kfxpcyapdle9aoqe02q` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
DROP TABLE IF EXISTS `cart_item`;
CREATE TABLE `cart_item` (
  `id` bigint NOT NULL,
  `quantity` int NOT NULL,
  `total_price` int DEFAULT NULL,
  `cart_id` bigint DEFAULT NULL,
  `food_id` bigint DEFAULT NULL,
  `restaurant_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKcro8349ry4i72h81en8iw202g` (`food_id`),
  KEY `FK1uobyhgl1wvgt1jpccia8xxs3` (`cart_id`),
  KEY `FK6su49qpifcyu0m1sicxfda1po` (`restaurant_id`),
  CONSTRAINT `FK1uobyhgl1wvgt1jpccia8xxs3` FOREIGN KEY (`cart_id`) REFERENCES `cart` (`id`),
  CONSTRAINT `FK6su49qpifcyu0m1sicxfda1po` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant` (`id`),
  CONSTRAINT `FKcro8349ry4i72h81en8iw202g` FOREIGN KEY (`food_id`) REFERENCES `food` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
DROP TABLE IF EXISTS `cart_item_food`;
CREATE TABLE `cart_item_food` (
  `cart_item_id` bigint NOT NULL,
  `food_id` bigint NOT NULL,
  KEY `FK9mmkfld4pqect5lmn49f2d1l9` (`food_id`),
  KEY `FKbo2e3mksx1lmqma10f5exwims` (`cart_item_id`),
  CONSTRAINT `FK9mmkfld4pqect5lmn49f2d1l9` FOREIGN KEY (`food_id`) REFERENCES `food` (`id`),
  CONSTRAINT `FKbo2e3mksx1lmqma10f5exwims` FOREIGN KEY (`cart_item_id`) REFERENCES `cart_item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
  `id` bigint NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK46ccwnsi9409t36lurvtyljak` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
DROP TABLE IF EXISTS `cuisine`;
CREATE TABLE `cuisine` (
  `id` bigint NOT NULL,
  `restaurant_id` bigint DEFAULT NULL,
  `cuisine_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK5hbmpav3qhgomtxd4nw1ckvwj` (`restaurant_id`),
  CONSTRAINT `FK5hbmpav3qhgomtxd4nw1ckvwj` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
DROP TABLE IF EXISTS `food`;
CREATE TABLE `food` (
  `id` bigint NOT NULL,
  `available` bit(1) NOT NULL,
  `creation_date` date DEFAULT NULL,
  `description` varchar(500) DEFAULT NULL,
  `is_seasonal` bit(1) NOT NULL,
  `is_vegetarian` bit(1) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `price` int DEFAULT NULL,
  `food_category_id` bigint DEFAULT NULL,
  `restaurant_id` bigint DEFAULT NULL,
  `image_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKd5jb57wcj3nomso10nhrit3dc` (`food_category_id`),
  KEY `FKm9xrxt95wwp1r2s7andom1l1c` (`restaurant_id`),
  CONSTRAINT `FKd5jb57wcj3nomso10nhrit3dc` FOREIGN KEY (`food_category_id`) REFERENCES `category` (`id`),
  CONSTRAINT `FKm9xrxt95wwp1r2s7andom1l1c` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
DROP TABLE IF EXISTS `food_images`;
CREATE TABLE `food_images` (
  `food_id` bigint NOT NULL,
  `images` varchar(1000) DEFAULT NULL,
  KEY `FKjjjt9373et45vaj0mguo4pd2p` (`food_id`),
  CONSTRAINT `FKjjjt9373et45vaj0mguo4pd2p` FOREIGN KEY (`food_id`) REFERENCES `food` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
DROP TABLE IF EXISTS `food_ingredients`;
CREATE TABLE `food_ingredients` (
  `food_id` bigint NOT NULL,
  `ingredients_id` bigint NOT NULL,
  KEY `FKhy3t7b303ydmureccjf1qak2k` (`ingredients_id`),
  KEY `FKnfwd9dp2aw8o8l4ftu39jmvv9` (`food_id`),
  CONSTRAINT `FKhy3t7b303ydmureccjf1qak2k` FOREIGN KEY (`ingredients_id`) REFERENCES `ingredients_item` (`id`),
  CONSTRAINT `FKnfwd9dp2aw8o8l4ftu39jmvv9` FOREIGN KEY (`food_id`) REFERENCES `food` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
DROP TABLE IF EXISTS `food_types`;
CREATE TABLE `food_types` (
  `id` bigint NOT NULL,
  `image_id` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
DROP TABLE IF EXISTS `ingredient_category`;
CREATE TABLE `ingredient_category` (
  `id` bigint NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKaa4fbpvq1sv7vhegrlvi0cc2i` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
DROP TABLE IF EXISTS `ingredients_item`;
CREATE TABLE `ingredients_item` (
  `in_stock` bit(1) NOT NULL,
  `category_id` bigint DEFAULT NULL,
  `id` bigint NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKjb94f4rm414htlxd1mwhf56in` (`category_id`),
  CONSTRAINT `FKjb94f4rm414htlxd1mwhf56in` FOREIGN KEY (`category_id`) REFERENCES `ingredient_category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
DROP TABLE IF EXISTS `ingredients_item_food`;
CREATE TABLE `ingredients_item_food` (
  `ingredients_item_id` bigint NOT NULL,
  `food_id` bigint NOT NULL,
  KEY `FKfer8sajhnhvcosv2qy15qldkr` (`food_id`),
  KEY `FKmg25fy6q0yormmfja2mc31g97` (`ingredients_item_id`),
  CONSTRAINT `FKfer8sajhnhvcosv2qy15qldkr` FOREIGN KEY (`food_id`) REFERENCES `food` (`id`),
  CONSTRAINT `FKmg25fy6q0yormmfja2mc31g97` FOREIGN KEY (`ingredients_item_id`) REFERENCES `ingredients_item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
DROP TABLE IF EXISTS `native`;
CREATE TABLE `native` (
  `next_val` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
DROP TABLE IF EXISTS `offers`;
CREATE TABLE `offers` (
  `id` bigint NOT NULL,
  `coupon_code` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `discount_amount` int DEFAULT NULL,
  `discount_percentage` int DEFAULT NULL,
  `header` varchar(255) DEFAULT NULL,
  `minimum_order_value` int DEFAULT NULL,
  `offer_logo` varchar(255) DEFAULT NULL,
  `offer_type` varchar(255) DEFAULT NULL,
  `restaurant_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK4ko84qlqb2lpk4a2trsm9nk8b` (`restaurant_id`),
  CONSTRAINT `FK4ko84qlqb2lpk4a2trsm9nk8b` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
  `quantity` int NOT NULL,
  `food_id` bigint DEFAULT NULL,
  `id` bigint NOT NULL,
  `order_id` bigint DEFAULT NULL,
  `total_price` bigint DEFAULT NULL,
  `ingredients` varbinary(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKlg06lkamdhrbmjj40ump3ynw0` (`food_id`),
  KEY `FKtju8k1qf34vfhdda8mkskdw2m` (`order_id`),
  CONSTRAINT `FK4fcv9bk14o2k04wghr09jmy3b` FOREIGN KEY (`food_id`) REFERENCES `food` (`id`),
  CONSTRAINT `FKtju8k1qf34vfhdda8mkskdw2m` FOREIGN KEY (`order_id`) REFERENCES `orderr` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
DROP TABLE IF EXISTS `orderr`;
CREATE TABLE `orderr` (
  `created_at` date DEFAULT NULL,
  `total_item` int NOT NULL,
  `total_price` int NOT NULL,
  `address_id` bigint DEFAULT NULL,
  `customer_id` bigint DEFAULT NULL,
  `id` bigint NOT NULL,
  `restaurant_id` bigint DEFAULT NULL,
  `total_amount` bigint DEFAULT NULL,
  `order_status` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK46n573dd8b7uwlul34bsgptqa` (`customer_id`),
  KEY `FKdnqta1xjseur240sj1l7bgra4` (`address_id`),
  KEY `FKj2m2jddr502d4yipfvusritho` (`restaurant_id`),
  CONSTRAINT `FK46n573dd8b7uwlul34bsgptqa` FOREIGN KEY (`customer_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKdnqta1xjseur240sj1l7bgra4` FOREIGN KEY (`address_id`) REFERENCES `address` (`id`),
  CONSTRAINT `FKj2m2jddr502d4yipfvusritho` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
DROP TABLE IF EXISTS `restaurant`;
CREATE TABLE `restaurant` (
  `is_open` bit(1) NOT NULL,
  `id` bigint NOT NULL,
  `owner_id` bigint DEFAULT NULL,
  `registration_date` datetime(6) DEFAULT NULL,
  `area_name` varchar(255) DEFAULT NULL,
  `avg_rating_string` varchar(255) DEFAULT NULL,
  `cost_for_two` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `discount_info` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `image_id` varchar(255) DEFAULT NULL,
  `instagram` varchar(255) DEFAULT NULL,
  `locality` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `opening_hours` varchar(255) DEFAULT NULL,
  `total_ratings_string` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKe5wptm5diypt91i1wpsa42h6x` (`owner_id`),
  CONSTRAINT `FKnm7kj0jgjep1nm5rslxei79jl` FOREIGN KEY (`owner_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
DROP TABLE IF EXISTS `restaurant_images`;
CREATE TABLE `restaurant_images` (
  `restaurant_id` bigint NOT NULL,
  `images` varchar(1000) DEFAULT NULL,
  KEY `FK810i11orew47qx1nrcwlh43jb` (`restaurant_id`),
  CONSTRAINT `FK810i11orew47qx1nrcwlh43jb` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `user_role` tinyint DEFAULT NULL,
  `cart_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK47dq8urpj337d3o65l3fsjph3` (`cart_id`),
  CONSTRAINT `FKtqa69bib34k2c0jhe7afqsao6` FOREIGN KEY (`cart_id`) REFERENCES `cart` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
DROP TABLE IF EXISTS `user_address`;
CREATE TABLE `user_address` (
  `user_address_id` bigint NOT NULL AUTO_INCREMENT,
  `address_name` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `flat_no` varchar(255) DEFAULT NULL,
  `pin_code` varchar(255) DEFAULT NULL,
  `street` varchar(255) DEFAULT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  `validated_google_address` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`user_address_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
DROP TABLE IF EXISTS `user_favourites`;
CREATE TABLE `user_favourites` (
  `id` bigint DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `images` varbinary(1000) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  KEY `FKj2kht57b5ftwc4nkpn3vbc5b3` (`user_id`),
  CONSTRAINT `FKj2kht57b5ftwc4nkpn3vbc5b3` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
