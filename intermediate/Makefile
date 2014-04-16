all: report.pdf

# We run `pdflatex` twice to make sure all references are properly generated.
report.pdf: report.tex
	pdflatex $<
	bibtex report
	pdflatex $<
	pdflatex $<

clean:
	git clean -fdX

.PHONY: clean
