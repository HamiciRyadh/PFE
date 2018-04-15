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
  category_id integer,
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



CREATE TABLE public."Product"
(
  product_id SERIAL,
  product_name character varying(100) NOT NULL,
  product_type integer, 
  product_trade_mark character varying(100) NOT NULL,
  CONSTRAINT pk_product_id PRIMARY KEY (product_id),
  CONSTRAINT fk_product_type FOREIGN KEY (product_type)
      REFERENCES public."Type" (type_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public."Product"
  OWNER TO postgres;




CREATE TABLE public."SalesPoint"
(
  salespoint_id character varying(250) NOT NULL,

  CONSTRAINT "SalesPoint_pkey" PRIMARY KEY (salespoint_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public."SalesPoint"
  OWNER TO postgres;






CREATE TABLE public."ProductSalesPoint"
(
  salespoint_id character varying(250) NOT NULL,
  product_id SERIAL,
  product_quantity integer,
  product_price real,
  CONSTRAINT pk_productsalespoint_id PRIMARY KEY (salespoint_id, product_id),
  CONSTRAINT fk_product_id FOREIGN KEY (product_id)
      REFERENCES public."Product" (product_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_salespoint_id FOREIGN KEY (salespoint_id)
      REFERENCES public."SalesPoint" (salespoint_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT valid_price CHECK (product_price >= 0::double precision),
  CONSTRAINT valid_quantity CHECK (product_quantity >= 0)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public."ProductSalesPoint"
  OWNER TO postgres;




INSERT INTO public."Category"(category_id, category_name)
	VALUES (0,'Informatique');
INSERT INTO public."Category"(category_id, category_name)
	VALUES (1,'Téléphone');
INSERT INTO public."Category"(category_id, category_name)
	VALUES (2,'Électronique');
INSERT INTO public."Category"(category_id, category_name)
	VALUES (3,'Automobile');
INSERT INTO public."Category"(category_id, category_name)
	VALUES (4,'Matériel Professionnel');
  INSERT INTO public."Category"(category_id, category_name)
	VALUES (5,'Électroménager');
 



INSERT INTO public."Type"(type_id, type_name, category_id)
	VALUES (0,'Ordinateur Portable', 0);
INSERT INTO public."Type"(type_id, type_name, category_id)
	VALUES (1,'Imprimante & Scanner ', 0);
INSERT INTO public."Type"(type_id, type_name, category_id)
	VALUES (2,'Ordinateur de Bureau', 0);
INSERT INTO public."Type"(type_id, type_name, category_id)
	VALUES (3,'Écran  & Data Show', 0);
		
INSERT INTO public."Type"(type_id, type_name, category_id)
	VALUES (4,'Smartphone', 1);
INSERT INTO public."Type"(type_id, type_name, category_id)
	VALUES (5,'Tablette', 1);
INSERT INTO public."Type"(type_id, type_name, category_id)
	VALUES (6,'Téléphone fixe', 1);
  INSERT INTO public."Type"(type_id, type_name, category_id)
	VALUES (7,'Téléphone Portable', 1);




INSERT INTO public."Product"(product_id, product_name, product_type, product_trade_mark)
	VALUES (1,'ELITEBOOK 2570 I5-3360 4G 320 ', 0, 'HP');

  INSERT INTO public."Product"(product_id, product_name, product_type, product_trade_mark)
	VALUES (2,'SCRANJET PRO 2500 F1', 1, 'HP');

    INSERT INTO public."Product"(product_id, product_name, product_type, product_trade_mark)
	VALUES (3,'COMPAQ 8300', 2, 'HP');

      INSERT INTO public."Product"(product_id, product_name, product_type, product_trade_mark)
	VALUES (4,'PROBOOK 450 G2 i5 4GB 500', 0, 'HP');


      INSERT INTO public."Product"(product_id, product_name, product_type, product_trade_mark)
	VALUES (5,' THINKPAD L450 i5-5200U', 0, 'LENOVO');


      INSERT INTO public."Product"(product_id, product_name, product_type, product_trade_mark)
	VALUES (6,' LATITUDE 3560', 0, 'DELL');

      INSERT INTO public."Product"(product_id, product_name, product_type, product_trade_mark)
	VALUES (7,'E5-571 i3 5G 4GB 500GB', 0, 'ACER');


    INSERT INTO public."Product"(product_id, product_name, product_type, product_trade_mark)
	VALUES (8,'G50-80 i3 4GB 500GB', 0, 'LENOVO');

     INSERT INTO public."Product"(product_id, product_name, product_type, product_trade_mark)
	VALUES (9,'CHROMEBOOK EXYNOS 5250HD 16GO', 0, 'SAMSUNG');


    INSERT INTO public."Product"(product_id, product_name, product_type, product_trade_mark)
	VALUES (10,'GALAXY NOTE 5', 7, 'SAMSUNG');

    INSERT INTO public."Product"(product_id, product_name, product_type, product_trade_mark)
	VALUES (11,'GALAXY J7 PRO ', 7, 'SAMSUNG');

      INSERT INTO public."Product"(product_id, product_name, product_type, product_trade_mark)
	VALUES (12,'ATIV BOOK 9 900X3K', 0, 'SAMSUNG');




INSERT INTO public."SalesPoint"(salespoint_id) VALUES ('ChIJ_X0pNnMMjxIRBAPluP77cXY');
INSERT INTO public."SalesPoint"(salespoint_id) VALUES ('ChIJgbWj2jyxjxIRTEiyXgwGVqA');
INSERT INTO public."SalesPoint"(salespoint_id) VALUES ('ChIJeWdnsPetjxIRYJD8lV-bhFI');
INSERT INTO public."SalesPoint"(salespoint_id) VALUES ('ChIJlfTOBUOtjxIRgwNIAYmOxqU');
INSERT INTO public."SalesPoint"(salespoint_id) VALUES ('ChIJWwbqnpitjxIR3zoXR3V6VD0');
INSERT INTO public."SalesPoint"(salespoint_id) VALUES ('ChIJY3-2EAsMjxIRnTSLxl8fHGA');


INSERT INTO public."ProductSalesPoint"(salespoint_id, product_id, product_quantity, product_price)
	VALUES ('ChIJ_X0pNnMMjxIRBAPluP77cXY', 10,10 ,54000 );

  INSERT INTO public."ProductSalesPoint"(salespoint_id, product_id, product_quantity, product_price)
	VALUES ('ChIJ_X0pNnMMjxIRBAPluP77cXY',11, 0,48000 );

    INSERT INTO public."ProductSalesPoint"(salespoint_id, product_id, product_quantity, product_price)
	VALUES ('ChIJgbWj2jyxjxIRTEiyXgwGVqA', 1, 0,65000);

        INSERT INTO public."ProductSalesPoint"(salespoint_id, product_id, product_quantity, product_price)
	VALUES ('ChIJgbWj2jyxjxIRTEiyXgwGVqA', 5, 8,86500 );

  
    INSERT INTO public."ProductSalesPoint"(salespoint_id, product_id, product_quantity, product_price)
	VALUES ('ChIJgbWj2jyxjxIRTEiyXgwGVqA', 4, 10,58000 );

  
    INSERT INTO public."ProductSalesPoint"(salespoint_id, product_id, product_quantity, product_price)
	VALUES ('ChIJY3-2EAsMjxIRnTSLxl8fHGA', 4, 10,58000 );
INSERT INTO public."SalesPoint"(salespoint_id) VALUES ('ChIJY3-2EAsMjxIRnTSLxl8fHGA');

  INSERT INTO public."ProductSalesPoint"(salespoint_id, product_id, product_quantity, product_price)
	VALUES ('ChIJeWdnsPetjxIRYJD8lV-bhFI', 4, 0,55000 );

      INSERT INTO public."ProductSalesPoint"(salespoint_id, product_id, product_quantity, product_price)
	VALUES ('ChIJWwbqnpitjxIR3zoXR3V6VD0', 4, 2,55800 );

       INSERT INTO public."ProductSalesPoint"(salespoint_id, product_id, product_quantity, product_price)
	VALUES ('ChIJlfTOBUOtjxIRgwNIAYmOxqU', 4, 0,51800 );


        INSERT INTO public."ProductSalesPoint"(salespoint_id, product_id, product_quantity, product_price)
	VALUES ('ChIJgbWj2jyxjxIRTEiyXgwGVqA', 1, 8,86500 );

        INSERT INTO public."ProductSalesPoint"(salespoint_id, product_id, product_quantity, product_price)
	VALUES ('ChIJgbWj2jyxjxIRTEiyXgwGVqA', 2, 8,86500 );
        INSERT INTO public."ProductSalesPoint"(salespoint_id, product_id, product_quantity, product_price)
	VALUES ('ChIJgbWj2jyxjxIRTEiyXgwGVqA', 3, 8,86500 );
        INSERT INTO public."ProductSalesPoint"(salespoint_id, product_id, product_quantity, product_price)
	VALUES ('ChIJgbWj2jyxjxIRTEiyXgwGVqA', 6, 8,86500 );

        INSERT INTO public."ProductSalesPoint"(salespoint_id, product_id, product_quantity, product_price)
	VALUES ('ChIJgbWj2jyxjxIRTEiyXgwGVqA', 7, 8,86500 );

        INSERT INTO public."ProductSalesPoint"(salespoint_id, product_id, product_quantity, product_price)
	VALUES ('ChIJgbWj2jyxjxIRTEiyXgwGVqA', 8, 8,86500 );

          INSERT INTO public."ProductSalesPoint"(salespoint_id, product_id, product_quantity, product_price)
	VALUES ('ChIJgbWj2jyxjxIRTEiyXgwGVqA', 9, 8,86500 );

  
  

  

  

  

  








