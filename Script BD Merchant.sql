CREATE TABLE public."Product"
(
  product_barcode character varying(100) NOT NULL,
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

CREATE OR REPLACE FUNCTION updateProductSalesPoint() RETURNS TRIGGER AS $updatepsp$
    BEGIN
  RAISE NOTICE 'BEGINNING';
        IF (TG_OP = 'UPDATE') THEN
            PERFORM * FROM http((
            'POST',
            'http://192.168.1.6:8080/PFE-EE/api/ProductSalesPoint/UpdateProductSalesPoint' ||
            '?sales_point_id=' || 'ChIJgbWj2jyxjxIRTEiyXgwGVqA' ||
            '&product_barcode=' || NEW.product_barcode ||
            '&product_quantity_old=' || OLD.product_quantity ||
            '&product_price_old=' || OLD.product_price ||
            '&product_quantity_new=' || NEW.product_quantity ||
            '&product_price_new=' || NEW.product_price,
             ARRAY[http_header('Authorization','Basic bWVyY2hhbnRAbGZic2VydmljZXMuY29tOmFkbWlu')],
             '',
             'application/x-form-urlencoded'
          )::http_request);
            RAISE NOTICE 'GOOD END';
            RETURN NEW;
        END IF;
        RAISE NOTICE 'BAD END';
        RETURN NULL;
    END;
$updatepsp$ LANGUAGE plpgsql;

CREATE TRIGGER updateProductSalesPoint
AFTER UPDATE ON public."Product" FOR EACH ROW EXECUTE PROCEDURE updateProductSalesPoint();


CREATE OR REPLACE FUNCTION removeProductSalesPoint() RETURNS TRIGGER AS $removepsp$
    BEGIN
  RAISE NOTICE 'BEGINNING';
        IF (TG_OP = 'UPDATE') THEN
            PERFORM * FROM http((
            'DELETE',
            'http://192.168.1.6:8080/PFE-EE/api/ProductSalesPoint/RemoveProductSalesPoint' ||
            '?sales_point_id=' || 'ChIJgbWj2jyxjxIRTEiyXgwGVqA' ||
            '&product_barcode=' || OLD.product_barcode,
             ARRAY[http_header('Authorization','Basic bWVyY2hhbnRAbGZic2VydmljZXMuY29tOmFkbWlu')],
             '',
             'application/x-form-urlencoded'
          )::http_request);
            RAISE NOTICE 'GOOD END';
            RETURN NEW;
        END IF;
        RAISE NOTICE 'BAD END';
        RETURN NULL;
    END;
$removepsp$ LANGUAGE plpgsql;



CREATE TRIGGER removeProductSalesPoint
AFTER DELETE ON public."Product" FOR EACH ROW EXECUTE PROCEDURE removeProductSalesPoint();