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


CREATE TABLE public."Wilaya"
(
  wilaya_id integer NOT NULL,
  wilaya_name character varying(50) NOT NULL,
  CONSTRAINT "Wilaya_pkey" PRIMARY KEY (wilaya_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public."Wilaya"
  OWNER TO postgres;


CREATE TABLE public."City"
(
  city_id integer NOT NULL,
  city_name character varying(50) NOT NULL,
  wilaya_id integer NOT NULL,
  CONSTRAINT "City_pkey" PRIMARY KEY (city_id),
  CONSTRAINT fk_city_wilaya_id FOREIGN KEY (wilaya_id)
      REFERENCES public."Wilaya" (wilaya_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public."City"
  OWNER TO postgres;



CREATE TABLE public."SalesPoint"
(
  sales_point_id character varying(250) NOT NULL,
  sales_point_name character varying(250) NOT NULL,
  sales_point_lat real NOT NULL,
  sales_point_lng real NOT NULL,
  sales_point_address character varying(250) NOT NULL,
  city_id integer NOT NULL,
  sales_point_phone_number character varying(30) NOT NULL,
  sales_point_website character varying(250) NOT NULL,
  sales_point_rating real NOT NULL,
  sales_point_picture OID NOT NULL,
  CONSTRAINT "SalesPoint_pkey" PRIMARY KEY (sales_point_id),
  CONSTRAINT fk_sales_point_city_id FOREIGN KEY (city_id)
      REFERENCES public."City" (city_id) MATCH SIMPLE
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


CREATE TABLE public."Notifications"
(
  sales_point_id character varying(250) NOT NULL,
  product_barcode character varying(50) NOT NULL,
  notification_date_time timestamp NOT NULL,
  notification_new_quantity integer NOT NULL,
  notification_new_price real NOT NULL,
  CONSTRAINT "Notifications_pkey" PRIMARY KEY (sales_point_id, product_barcode),
  CONSTRAINT fk_sales_point_id_product_barcode FOREIGN KEY (sales_point_id, product_barcode)
      REFERENCES public."ProductSalesPoint" (sales_point_id, product_barcode) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public."Notifications"
  OWNER TO postgres;


CREATE TABLE public."History"
(
  history_id SERIAL,
  history_query character varying(100) NOT NULL,
  CONSTRAINT "History_pkey" PRIMARY KEY (history_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public."History"
  OWNER TO postgres;