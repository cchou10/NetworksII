all: final.pdf

# We run `pdflatex` twice to make sure all references are properly generated.
final.pdf: final.tex
	pdflatex $<
	bibtex final
	pdflatex $<
	pdflatex $<

clean:
	git clean -fdX

.PHONY: clean
