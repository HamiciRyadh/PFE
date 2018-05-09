CREATE TABLE public."Product"
(
  product_barcode character varying(50) NOT NULL,
  product_name character varying(100) NOT NULL,
  product_trade_mark character varying(100) NOT NULL,
  product_quantity integer NOT NULL,
  product_price real NOT NULL,
  CONSTRAINT pk_product_barcode PRIMARY KEY (product_barcode)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public."Product"
  OWNER TO postgres;