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

INSERT INTO public."User"(user_mail_address, user_password)
	VALUES ('hamiciryadh@gmail.com', 'admin');

INSERT INTO public."User"(user_mail_address, user_password)
	VALUES ('bradai.imene@outlook.com', 'admin');