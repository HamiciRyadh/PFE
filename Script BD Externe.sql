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
  product_barcode character varying(50) NOT NULL,
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
  product_barcode character varying(50) NOT NULL,
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
  admin_password character varying(100) NOT NULL,
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
  merchant_password character varying(100) NOT NULL,
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
  product_barcode character varying(50) NOT NULL,
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
  user_password character varying(100) NOT NULL,
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
  product_barcode character varying(50) NOT NULL,
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
  (2, 'Appreil Photo'),
  (3, 'Automobile'),
  (4, 'Matériel Professionnel'),
  (5, 'Montre');


INSERT INTO public."Type"(type_id, type_name, category_id) VALUES 
  (0, 'Ordinateur Portable', 0),
  (1, 'Imprimante & Scanner ', 0),
  (2, 'Ordinateur de Bureau', 0),
  (3, 'Écran  & Data Show', 0),
  (4, 'Smartphone', 1),
  (5, 'Tablette', 1),
  (6, 'Téléphone fixe', 1),
  (7, 'Téléphone Portable', 1);


INSERT INTO public."Product"(product_barcode, product_name, product_type, product_trade_mark) VALUES 
  ('1', 'ELITEBOOK 2570 I5-3360 4G 320 ', 0, 'HP'),
  ('2', 'SCRANJET PRO 2500 F1', 1, 'HP'),
  ('3', 'COMPAQ 8300', 2, 'HP'),
  ('4', 'PROBOOK 450 G2 i5 4GB 500', 0, 'HP'),
  ('5', 'THINKPAD L450 i5-5200U', 0, 'LENOVO'),
  ('6', 'LATITUDE 3560', 0, 'DELL'),
  ('7', 'E5-571 i3 5G 4GB 500GB', 0, 'ACER'),
  ('8', 'G50-80 i3 4GB 500GB', 0, 'LENOVO'),
  ('9', 'CHROMEBOOK EXYNOS 5250HD 16GO', 0, 'SAMSUNG'),
  ('10', 'GALAXY NOTE 5', 7, 'SAMSUNG'),
  ('R28J23QCXGZ', 'GALAXY J7 PRIME ', 7, 'SAMSUNG'),
  ('12', 'ATIV BOOK 9 900X3K', 0, 'SAMSUNG');


#Password is admin for every user
INSERT INTO public."User"(user_mail_address, user_password) VALUES 
  ('hamiciryadh@gmail.com', 'd033e22ae348aeb5660fc2140aec35850c4da997'),
  ('bradai.imene@outlook.fr', 'd033e22ae348aeb5660fc2140aec35850c4da997');

INSERT INTO public."Administrator"(admin_id, admin_mail_address, admin_password) VALUES 
  (0, 'admin@pfe.com', 'd033e22ae348aeb5660fc2140aec35850c4da997');

INSERT INTO public."Merchant"(merchant_id, merchant_mail_address, merchant_password, admin_id) VALUES 
  (0, 'merchant@lfbservices.com', 'd033e22ae348aeb5660fc2140aec35850c4da997', 0);


INSERT INTO public."SalesPoint"(sales_point_id, merchant_id) VALUES 
  ('ChIJ_X0pNnMMjxIRBAPluP77cXY', 0),
  ('ChIJgbWj2jyxjxIRTEiyXgwGVqA', 0),
  ('ChIJeWdnsPetjxIRYJD8lV-bhFI', 0),
  ('ChIJlfTOBUOtjxIRgwNIAYmOxqU', 0),
  ('ChIJWwbqnpitjxIR3zoXR3V6VD0', 0),
  ('ChIJraM3b5lwjxIRiCsvRUEF9DQ', 0);


INSERT INTO public."ProductSalesPoint"(sales_point_id, product_barcode, product_quantity, product_price) VALUES 
  ('ChIJ_X0pNnMMjxIRBAPluP77cXY', '10', 10 ,54000),
  ('ChIJ_X0pNnMMjxIRBAPluP77cXY', 'R28J23QCXGZ', 0,48000),
  ('ChIJgbWj2jyxjxIRTEiyXgwGVqA', '1', 0,65000),
  ('ChIJgbWj2jyxjxIRTEiyXgwGVqA', '5', 8,86500),
  ('ChIJgbWj2jyxjxIRTEiyXgwGVqA', '4', 10,58000),
  ('ChIJeWdnsPetjxIRYJD8lV-bhFI', '4', 0,55000),
  ('ChIJWwbqnpitjxIR3zoXR3V6VD0', '4', 2,55800),
  ('ChIJlfTOBUOtjxIRgwNIAYmOxqU', '4', 0,51800),
  ('ChIJraM3b5lwjxIRiCsvRUEF9DQ', '4', 15,54500);


INSERT INTO public."TypeCaracteristic"(type_caracteristic_id, type_id, type_caracteristic_name) VALUES 
  (1, 0, 'CPU'),
  (2, 0, 'ECRAN'),
  (3, 0, 'RAM'),
  (4, 0, 'CARTE GRAPHIQUE');

     
INSERT INTO public."ProductCaracteristic"(type_caracteristic_id, product_barcode, product_caracteristic_value) VALUES 
  (1, '4', 'Intel Core i3-4005U'),
  (2, '4', '15.6 pouces'),
  (3, '4', '4 GO'),
  (4, '4', 'Radeon HD 8670M Dual');