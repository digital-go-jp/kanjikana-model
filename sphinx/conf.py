# Configuration file for the Sphinx documentation builder.
#
# For the full list of built-in configuration values, see the documentation:
# https://www.sphinx-doc.org/en/master/usage/configuration.html

# -- Project information -----------------------------------------------------
# https://www.sphinx-doc.org/en/master/usage/configuration.html#project-information

project = 'kanjikana-model'
copyright = '2024, Digital Agency'
author = 'Digital Agency'

# -- General configuration ---------------------------------------------------
# https://www.sphinx-doc.org/en/master/usage/configuration.html#general-configuration

extensions = [
  'sphinxcontrib.seqdiag',
  'sphinxcontrib.mermaid',
  'sphinx_rtd_theme',
]

templates_path = ['_templates']
exclude_patterns = ['_build', 'Thumbs.db', '.DS_Store']

language = 'ja'

# -- Options for HTML output -------------------------------------------------
# https://www.sphinx-doc.org/en/master/usage/configuration.html#options-for-html-output

#html_theme = 'alabaster'
html_theme = 'sphinx_rtd_theme'
#html_theme ='pydata_sphinx_theme'
html_static_path = ['_static']

html_css_files = [
    'custom.css',
]

extensions += ['sphinx.ext.imgmath']
imgmath_image_format = 'svg'
imgmath_font_size = 14
pngmath_latex='platex'

numfig=True