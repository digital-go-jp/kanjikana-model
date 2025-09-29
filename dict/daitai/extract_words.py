from lxml import html


def parse_html(fname):
    with open(fname,'r',encoding='utf-8') as f:
        lines="".join([l.rstrip() for l in f])

    root=html.fromstring(lines)


    lst=[]
    words = root.xpath('//div[@class="parts_box"]/ul[@class="search_parts"]/li/a')
    for word in words:
        lst.append(word.text)

    return lst

dai1=parse_html('data/jisdai1')