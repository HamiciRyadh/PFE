CREATE TABLE public."Category"
(
  category_id SERIAL,
  category_name character varying(100) NOT NULL,
  CONSTRAINT "Category_pkey" PRIMARY KEY (category_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public."Category"
  OWNER TO postgres;


CREATE TABLE public."Type"
(
  type_id SERIAL,
  type_name character varying(100) NOT NULL,
  category_id integer NOT NULL,
  CONSTRAINT "Type_pkey" PRIMARY KEY (type_id),
  CONSTRAINT fk_category FOREIGN KEY (category_id)
      REFERENCES public."Category" (category_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public."Type"
  OWNER TO postgres;


CREATE TABLE public."TypeKeyword"
(
  type_keyword_id SERIAL,
  type_id integer NOT NULL,
  keyword character varying(100) NOT NULL,
  CONSTRAINT "TypeKeyword_pkey" PRIMARY KEY (type_keyword_id),
  CONSTRAINT fk_type_id FOREIGN KEY (type_id)
      REFERENCES public."Type" (type_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public."TypeKeyword"
  OWNER TO postgres;


CREATE TABLE public."TypeCaracteristic"
(
  type_caracteristic_id SERIAL,
  type_id integer NOT NULL,
  type_caracteristic_name character varying(100) NOT NULL,
  CONSTRAINT "TypeCaracteristic_pkey" PRIMARY KEY (type_caracteristic_id),
  CONSTRAINT fk_type_id FOREIGN KEY (type_id)
      REFERENCES public."Type" (type_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public."TypeCaracteristic"
  OWNER TO postgres;


CREATE TABLE public."Product"
(
  product_barcode character varying(100) NOT NULL,
  product_name character varying(100) NOT NULL,
  product_type integer NOT NULL, 
  product_trade_mark character varying(100) NOT NULL,
  CONSTRAINT pk_product_barcode PRIMARY KEY (product_barcode),
  CONSTRAINT fk_product_type FOREIGN KEY (product_type)
      REFERENCES public."Type" (type_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public."Product"
  OWNER TO postgres;


CREATE TABLE public."ProductCaracteristic"
(
  type_caracteristic_id integer NOT NULL,
  product_barcode character varying(100) NOT NULL,
  product_caracteristic_value character varying(100) NOT NULL,
  CONSTRAINT "ProductCaracteristic_pkey" PRIMARY KEY (type_caracteristic_id, product_barcode),
  CONSTRAINT fk_type_caracteristic_id FOREIGN KEY (type_caracteristic_id)
      REFERENCES public."TypeCaracteristic" (type_caracteristic_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_product_barcode FOREIGN KEY (product_barcode)
      REFERENCES public."Product" (product_barcode) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public."ProductCaracteristic"
  OWNER TO postgres;



CREATE TABLE public."Administrator"
(
  admin_id SERIAL,
  admin_mail_address character varying(100) UNIQUE NOT NULL,
  admin_password character varying(250) NOT NULL,
  CONSTRAINT "pk_admin" PRIMARY KEY (admin_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public."Administrator"
  OWNER TO postgres;


CREATE TABLE public."Merchant"
(
  merchant_id SERIAL,
  merchant_mail_address character varying(100) UNIQUE NOT NULL,
  merchant_password character varying(250) NOT NULL,
  admin_id integer NOT NULL,
  CONSTRAINT "pk_merchant" PRIMARY KEY (merchant_id),
  CONSTRAINT fk_admin_id FOREIGN KEY (admin_id)
      REFERENCES public."Administrator" (admin_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public."Merchant"
  OWNER TO postgres;


CREATE TABLE public."SalesPoint"
(
  sales_point_id character varying(250) NOT NULL,
  sales_point_name character varying(250),
  sales_point_lat real,
  sales_point_lng real,
  merchant_id integer,
  CONSTRAINT "SalesPoint_pkey" PRIMARY KEY (sales_point_id),
  CONSTRAINT fk_merchant_id FOREIGN KEY (merchant_id)
      REFERENCES public."Merchant" (merchant_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public."SalesPoint"
  OWNER TO postgres;


CREATE TABLE public."ProductSalesPoint"
(
  sales_point_id character varying(250) NOT NULL,
  product_barcode character varying(100) NOT NULL,
  product_quantity integer NOT NULL,
  product_price real NOT NULL,
  CONSTRAINT pk_product_sales_point_id PRIMARY KEY (sales_point_id, product_barcode),
  CONSTRAINT fk_product_barcode FOREIGN KEY (product_barcode)
      REFERENCES public."Product" (product_barcode) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_sales_point_id FOREIGN KEY (sales_point_id)
      REFERENCES public."SalesPoint" (sales_point_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT valid_price CHECK (product_price >= 0::double precision),
  CONSTRAINT valid_quantity CHECK (product_quantity >= 0)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public."ProductSalesPoint"
  OWNER TO postgres;


CREATE TABLE public."User"
(
  user_id SERIAL,
  user_mail_address character varying(100) UNIQUE NOT NULL,
  user_password character varying(250) NOT NULL,
  CONSTRAINT "pk_user" PRIMARY KEY (user_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public."User"
  OWNER TO postgres;


CREATE TABLE public."Notifications" 
(
  sales_point_id character varying(250) NOT NULL,
  product_barcode character varying(100) NOT NULL,
  user_id integer NOT NULL,
  CONSTRAINT "Notifications_pkey" PRIMARY KEY (sales_point_id, product_barcode, user_id),
  CONSTRAINT fk_sales_point_id_product_barcode FOREIGN KEY (sales_point_id, product_barcode)
      REFERENCES public."ProductSalesPoint" (sales_point_id, product_barcode) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_user_id FOREIGN KEY (user_id)
      REFERENCES public."User" (user_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public."Notifications"
  OWNER TO postgres;


CREATE TABLE public."UserDevice"
(
  user_id integer NOT NULL,
  user_device_id character varying(250) NOT NULL,
  CONSTRAINT "UserDevice_pkey" PRIMARY KEY (user_id, user_device_id),
  CONSTRAINT fk_user_id FOREIGN KEY (user_id)
      REFERENCES public."User" (user_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public."UserDevice"
  OWNER TO postgres;



INSERT INTO public."Category"(category_id, category_name) VALUES 
  (0, 'Informatique'),
  (1, 'Téléphone'),
  (2, 'Montre'),
  (3, 'Automobile'),
  (4, 'Matériel Professionnel'),
  (5, 'Appreil Photo');


INSERT INTO public."Type"(type_id, type_name, category_id) VALUES 
  (0, 'Ordinateur Portable', 0),
  (1, 'Imprimante & Scanner ', 0),
  (2, 'Ordinateur de Bureau', 0),
  (3, 'Écran  & Data Show', 0),
  (4, 'Smartphone', 1),
  (5, 'Tablette', 1),
  (6, 'Téléphone Fixe', 1),
  (7, 'Téléphone Portable', 1),
  (8, 'Montre pour homme', 2),
  (9, 'Montre pour femme', 2),
  (10, 'Montre pour enfant', 2),
  (11, '2 roues', 3),
  (12, '4 routes', 3);


INSERT INTO public."Product"(product_barcode, product_name, product_type, product_trade_mark) VALUES 
  ('1', 'ELITEBOOK 2570', 0, 'HP'),
  ('4', 'PROBOOK 450', 0, 'HP'),
  ('5', 'THINKPAD L450', 0, 'LENOVO'),
  ('6', 'LATITUDE 3560', 0, 'DELL'),
  ('7', 'E5-571', 0, 'ACER'),
  ('8', 'G50-80', 0, 'LENOVO'),
  ('9', 'CHROMEBOOK EXYNOS 5250HD', 0, 'SAMSUNG'),
  ('11', 'ATIV BOOK 9 900X3K', 0, 'SAMSUNG'),
  ('2', 'SCRANJET PRO 2500 F1', 1, 'HP'),
  ('3', 'COMPAQ 8300', 2, 'HP'),
  ('10', 'GALAXY NOTE 5', 4, 'SAMSUNG'),
  ('R28J23QCXGZ', 'GALAXY J7 PRIME ', 4, 'SAMSUNG'),
  ('14', 'GALAXY S5', 4, 'SAMSUNG'),
  ('15', 'P8 LITE', 4, 'CONDOR'),
  ('16', 'A9', 4, 'CONDOR'),
  ('17', 'P6 PRO', 4, 'CONDOR'),
  ('18', 'HONOR 8', 4, 'HUAWEI');


INSERT INTO public."User"(user_mail_address, user_password) VALUES 
  ('hamiciryadh@gmail.com', 'd033e22ae348aeb5660fc2140aec35850c4da997'),
  ('bradai.imene@outlook.fr', 'd033e22ae348aeb5660fc2140aec35850c4da997');

INSERT INTO public."Administrator"(admin_id, admin_mail_address, admin_password) VALUES 
  (0, 'admin@pfe.com', 'd033e22ae348aeb5660fc2140aec35850c4da997');

INSERT INTO public."Merchant"(merchant_id, merchant_mail_address, merchant_password, admin_id) VALUES 
  (0, 'merchant@lfbservices.com', 'd033e22ae348aeb5660fc2140aec35850c4da997', 0);


INSERT INTO public."SalesPoint"(sales_point_id, sales_point_name, sales_point_lat, sales_point_lng, merchant_id) VALUES 
  ('ChIJ_X0pNnMMjxIRBAPluP77cXY', 'SHOWROOM SAMSUNG BLIDA', 36.4826, 2.83732, 0),
  ('ChIJgbWj2jyxjxIRTEiyXgwGVqA', 'RuePC', 36.8125, 2.98826, 0),
  ('ChIJeWdnsPetjxIRYJD8lV-bhFI', 'Algerpc.com', 36.7418, 3.02971, 0),
  ('ChIJlfTOBUOtjxIRgwNIAYmOxqU', 'KOUBA COMPUTER', 36.7292, 3.08066, 0),
  ('ChIJWwbqnpitjxIR3zoXR3V6VD0', 'ncis informatique', 36.7202, 3.05087, 0),
  ('ChIJraM3b5lwjxIRiCsvRUEF9DQ', 'Solution Informatique', 36.4679, 2.62547, 0);


INSERT INTO public."ProductSalesPoint"(sales_point_id, product_barcode, product_quantity, product_price) VALUES 
  ('ChIJ_X0pNnMMjxIRBAPluP77cXY', '1', 10 ,54000),
  ('ChIJ_X0pNnMMjxIRBAPluP77cXY', '2', 0,48000),
  ('ChIJ_X0pNnMMjxIRBAPluP77cXY', '3', 0,65000),
  ('ChIJ_X0pNnMMjxIRBAPluP77cXY', '4', 8,86500),
  ('ChIJ_X0pNnMMjxIRBAPluP77cXY', '5', 10,58000),
  ('ChIJ_X0pNnMMjxIRBAPluP77cXY', '6', 0,55000),
  ('ChIJ_X0pNnMMjxIRBAPluP77cXY', '10', 15,54500),
  ('ChIJ_X0pNnMMjxIRBAPluP77cXY', '11', 15,54500),
  ('ChIJ_X0pNnMMjxIRBAPluP77cXY', '14', 0,55000),
  ('ChIJ_X0pNnMMjxIRBAPluP77cXY', '15', 15,54500),
  ('ChIJ_X0pNnMMjxIRBAPluP77cXY', '16', 15,54500),
  ('ChIJ_X0pNnMMjxIRBAPluP77cXY', 'R28J23QCXGZ', 15,54500),
  ('ChIJgbWj2jyxjxIRTEiyXgwGVqA', '1', 10 ,54000),
  ('ChIJgbWj2jyxjxIRTEiyXgwGVqA', '2', 0,48000),
  ('ChIJgbWj2jyxjxIRTEiyXgwGVqA', '3', 0,65000),
  ('ChIJgbWj2jyxjxIRTEiyXgwGVqA', '6', 0,55000),
  ('ChIJgbWj2jyxjxIRTEiyXgwGVqA', '8', 0,51800),
  ('ChIJgbWj2jyxjxIRTEiyXgwGVqA', '9', 15,54500),
  ('ChIJgbWj2jyxjxIRTEiyXgwGVqA', '11', 15,54500),
  ('ChIJgbWj2jyxjxIRTEiyXgwGVqA', '17', 15,54500),
  ('ChIJgbWj2jyxjxIRTEiyXgwGVqA', '18', 15,54500),
  ('ChIJgbWj2jyxjxIRTEiyXgwGVqA', 'R28J23QCXGZ', 15,54500),
  ('ChIJeWdnsPetjxIRYJD8lV-bhFI', '1', 10 ,54000),
  ('ChIJeWdnsPetjxIRYJD8lV-bhFI', '4', 8,86500),
  ('ChIJeWdnsPetjxIRYJD8lV-bhFI', '5', 10,58000),
  ('ChIJeWdnsPetjxIRYJD8lV-bhFI', '7', 2,55800),
  ('ChIJeWdnsPetjxIRYJD8lV-bhFI', '8', 0,51800),
  ('ChIJeWdnsPetjxIRYJD8lV-bhFI', '9', 15,54500),
  ('ChIJeWdnsPetjxIRYJD8lV-bhFI', '10', 15,54500),
  ('ChIJeWdnsPetjxIRYJD8lV-bhFI', '11', 15,54500),
  ('ChIJeWdnsPetjxIRYJD8lV-bhFI', '15', 15,54500),
  ('ChIJeWdnsPetjxIRYJD8lV-bhFI', '18', 15,54500),
  ('ChIJeWdnsPetjxIRYJD8lV-bhFI', 'R28J23QCXGZ', 15,54500),
  ('ChIJWwbqnpitjxIR3zoXR3V6VD0', '1', 10 ,54000),
  ('ChIJWwbqnpitjxIR3zoXR3V6VD0', '2', 0,48000),
  ('ChIJWwbqnpitjxIR3zoXR3V6VD0', '3', 0,65000),
  ('ChIJWwbqnpitjxIR3zoXR3V6VD0', '4', 8,86500),
  ('ChIJWwbqnpitjxIR3zoXR3V6VD0', '5', 10,58000),
  ('ChIJWwbqnpitjxIR3zoXR3V6VD0', '6', 0,55000),
  ('ChIJWwbqnpitjxIR3zoXR3V6VD0', '9', 15,54500),
  ('ChIJWwbqnpitjxIR3zoXR3V6VD0', '10', 15,54500),
  ('ChIJWwbqnpitjxIR3zoXR3V6VD0', '11', 15,54500),
  ('ChIJWwbqnpitjxIR3zoXR3V6VD0', '16', 15,54500),
  ('ChIJWwbqnpitjxIR3zoXR3V6VD0', 'R28J23QCXGZ', 15,54500),
  ('ChIJraM3b5lwjxIRiCsvRUEF9DQ', '1', 10 ,54000),
  ('ChIJraM3b5lwjxIRiCsvRUEF9DQ', '2', 0,48000),
  ('ChIJraM3b5lwjxIRiCsvRUEF9DQ', '3', 0,65000),
  ('ChIJraM3b5lwjxIRiCsvRUEF9DQ', '4', 8,86500),
  ('ChIJraM3b5lwjxIRiCsvRUEF9DQ', '5', 10,58000),
  ('ChIJraM3b5lwjxIRiCsvRUEF9DQ', '8', 0,51800),
  ('ChIJraM3b5lwjxIRiCsvRUEF9DQ', '9', 15,54500),
  ('ChIJraM3b5lwjxIRiCsvRUEF9DQ', '10', 15,54500),
  ('ChIJWwbqnpitjxIR3zoXR3V6VD0', '17', 15,54500);


INSERT INTO public."TypeCaracteristic"(type_caracteristic_id, type_id, type_caracteristic_name) VALUES 
  (1, 0, 'CPU'),
  (2, 0, 'Mémoire'),
  (3, 0, 'RAM'),
  (4, 0, 'GPU'),
  (5, 0, 'Résolution'),
  (6, 0, 'Ecran'),
  (7, 0, 'Batterie'),
  (8, 0, 'Autonomie'),
  (9, 0, 'Système d''exploitation'),
  (10, 0, 'Webcam'),
  (11, 0, 'Clavier'),

  (12, 4, 'Mémoire'),
  (13, 4, 'RAM'),
  (14, 4, 'Résolution'),
  (15, 4, 'Ecran'),
  (16, 4, 'Batterie'),
  (17, 4, 'Autonomie'),
  (18, 4, 'Système d''exploitation'),
  (19, 4, 'Caméra principale'),
  (20, 4, 'Caméra secondaire');

     
INSERT INTO public."ProductCaracteristic"(type_caracteristic_id, product_barcode, product_caracteristic_value) VALUES 
  (1, '1', 'Intel Core i5 6500U'),
  (2, '1', '8 Go'),
  (3, '1', 'Radeon HD 8670M Dual'),
  (4, '1', 'FHD (1920*1080)'),
  (5, '1', '15.6"'),
  (6, '1', '6 Cellules 3000mAh'),
  (7, '1', '8 Heures'),
  (8, '1', 'Ubuntu'),
  (9, '1', 'Oui'),
  (10, '1', 'Qwerty'),

  (1, '5', 'Intel Core i5 6500U'),
  (2, '5', '8 Go'),
  (3, '5', 'Radeon HD 8670M Dual'),
  (4, '5', 'FHD (1920*1080)'),
  (5, '5', '15.6"'),
  (6, '5', '6 Cellules 3000mAh'),
  (7, '5', '8 Heures'),
  (8, '5', 'Fedora'),
  (9, '5', 'Oui'),
  (10, '5', 'Qwerty'),

  (1, '4', 'Intel Core i3 4005U'),
  (2, '4', '4 Go'),
  (3, '4', 'Radeon HD 8670M Dual'),
  (4, '4', 'FHD (1920*1080)'),
  (5, '4', '15.6"'),
  (6, '4', '4 Cellules 2600mAh'),
  (7, '4', '6 Heures'),
  (8, '4', 'Windows 10'),
  (9, '4', 'Oui'),
  (10, '4', 'Azerty'),

  (1, '6', 'Intel Core i3 4005U'),
  (2, '6', '4 Go'),
  (3, '6', 'Radeon HD 8670M Dual'),
  (4, '6', 'FHD (1920*1080)'),
  (5, '6', '15.6"'),
  (6, '6', '4 Cellules 2600mAh'),
  (7, '6', '6 Heures'),
  (8, '6', 'Ubuntu'),
  (9, '6', 'Oui'),
  (10, '6', 'Azerty'),

  (1, '7', 'Intel Core i7 7780HQ'),
  (2, '7', '16 Go'),
  (3, '7', 'GTX 1050'),
  (4, '7', '4K'),
  (5, '7', '17,3"'),
  (6, '7', '8 Cellules 3000mAh'),
  (7, '7', '6 Heures'),
  (8, '7', 'Ubuntu'),
  (9, '7', 'Oui'),
  (10, '7', 'Qwerty'),

  (1, '8', 'Intel Core i3 4005U'),
  (2, '8', '4 Go'),
  (3, '8', 'Radeon HD 8670M Dual'),
  (4, '8', 'FHD (1920*1080)'),
  (5, '8', '13,3"'),
  (6, '8', '4 Cellules 2600mAh'),
  (7, '8', '6 Heures'),
  (8, '8', 'Ubuntu'),
  (9, '8', 'Oui'),
  (10, '8', 'Azerty'),

  (1, '9', 'Intel Core i3 4005U'),
  (2, '9', '4 Go'),
  (3, '9', 'Radeon HD 8670M Dual'),
  (4, '9', 'FHD (1920*1080)'),
  (5, '9', '13"'),
  (6, '9', '4 Cellules 2600mAh'),
  (7, '9', '6 Heures'),
  (8, '9', 'Windows 8'),
  (9, '9', 'Oui'),
  (10, '9', 'Azerty'),

  (1, '11', 'Intel Core i7 7500U'),
  (2, '11', '12 Go'),
  (3, '11', 'Radeon HD 8670M Dual'),
  (4, '11', 'FHD (1920*1080)'),
  (5, '11', '15.6"'),
  (6, '11', '4 Cellules 2600mAh'),
  (7, '11', '6 Heures'),
  (8, '11', 'WIndows 10'),
  (9, '11', 'Oui'),
  (10, '11', 'Azerty'),

  (12, '10', '32 Go'),
  (13, '10', '4 Go'),
  (14, '10', 'FHD (1920*1080)'),
  (15, '10', '5"'),
  (16, '10', '3000 mAh'),
  (17, '10', '18h'),
  (18, '10', 'Android'),
  (19, '10', '16 Mp'),
  (20, '10', '13 Mp'),

  (12, 'R28J23QCXGZ', '32 Go'),
  (13, 'R28J23QCXGZ', '4 Go'),
  (14, 'R28J23QCXGZ', 'FHD (1920*1080)'),
  (15, 'R28J23QCXGZ', '5"'),
  (16, 'R28J23QCXGZ', '3000 mAh'),
  (17, 'R28J23QCXGZ', '18h'),
  (18, 'R28J23QCXGZ', 'Android'),
  (19, 'R28J23QCXGZ', '16 Mp'),
  (20, 'R28J23QCXGZ', '13 Mp'),

  (12, '14', '16 Go'),
  (13, '14', '4 Go'),
  (14, '14', 'FHD (1920*1080)'),
  (15, '14', '5"'),
  (16, '14', '3000 mAh'),
  (17, '14', '18h'),
  (18, '14', 'Android'),
  (19, '14', '16 Mp'),
  (20, '14', '8 Mp'),

  (12, '15', '16 Go'),
  (13, '15', '4 Go'),
  (14, '15', 'FHD (1920*1080)'),
  (15, '15', '5"'),
  (16, '15', '2600 mAh'),
  (17, '15', '12h'),
  (18, '15', 'Android'),
  (19, '15', '13 Mp'),
  (20, '15', '8 Mp'),

  (12, '16', '16 Go'),
  (13, '16', '4 Go'),
  (14, '16', 'FHD (1920*1080)'),
  (15, '16', '5"'),
  (16, '16', '2600 mAh'),
  (17, '16', '12h'),
  (18, '16', 'Android'),
  (19, '16', '13 Mp'),
  (20, '16', '5 Mp'),

  (12, '17', '8 Go'),
  (13, '17', '4 Go'),
  (14, '17', 'FHD (1920*1080)'),
  (15, '17', '5"'),
  (16, '17', '2600 mAh'),
  (17, '17', '12h'),
  (18, '17', 'Android'),
  (19, '17', '13 Mp'),
  (20, '17', '5 Mp'),

  (12, '18', '18 Go'),
  (13, '18', '4 Go'),
  (14, '18', 'FHD (1920*1080)'),
  (15, '18', '5"'),
  (16, '18', '2600 mAh'),
  (17, '18', '12h'),
  (18, '18', 'Android'),
  (19, '18', '13 Mp'),
  (20, '18', '5 Mp');

INSERT INTO public."TypeKeyword" (type_keyword_id, type_id, keyword) VALUES 
  (0, 0, 'Laptop'),
  (1, 0, 'pc'),
  (2, 0, 'pc portable'),
  (3, 0, 'ordinateur'),
  (4, 0, 'netbook'),
  (5, 0, 'notebook'),
  (6, 0, 'probook'),
  (7, 0, 'gamer'),
  (8, 0, 'lifebook'),
  (9, 0, 'informatique'),
  (10, 4, 'smartphone'),
  (11, 4, 'android'),
  (12, 4, 'téléphone'),
  (13, 4, 'telephone'),
  (14, 4, 'portable'),
  (15, 4, 'telephone portable'),
  (16, 4, 'téléphone portable');