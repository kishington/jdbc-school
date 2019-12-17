CREATE TABLE IF NOT EXISTS public.students
(
    student_id integer,
    group_id integer,
    first_name character varying,
    last_name character varying,
    PRIMARY KEY (student_id)
);

ALTER TABLE public.students
    OWNER to "Sasha";
	