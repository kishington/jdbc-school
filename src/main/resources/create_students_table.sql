CREATE TABLE IF NOT EXISTS public.students
(
    student_id SERIAL PRIMARY KEY,
    group_id integer,
    first_name character varying,
    last_name character varying
);

ALTER TABLE public.students
    OWNER to "Sasha";
	