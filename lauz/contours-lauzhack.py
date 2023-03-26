import cv2
import numpy as np
import sys


def img_to_contour_svg(img_path = 'cut.jpg', svg_path = 'path.svg'):

    img = cv2.imread(img_path)

    # Grayscale
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

    # Find Canny edges
    edged = cv2.Canny(gray, 100, 400)

    contours, hierarchy = cv2.findContours(edged, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_NONE)

    print(len(contours))
    # print(contours)


    # c = max(contours, key=cv2.contourArea) #max contour
    sorted_c = sorted(contours, key=cv2.contourArea, reverse=True)
    c = sorted_c[0]
    # c2 = sorted_c[1]
    f = open(svg_path, 'w+')
    f.write('<svg width="'+str(img.shape[1])+'" height="'+str(img.shape[0])+'" xmlns="http://www.w3.org/2000/svg">')
    f.write('<path d="M')

    for con in sorted_c[:2]:
        for i in range(len(con)):
            x, y = con[i][0]
            f.write(str(x)+  ' ' + str(y)+' ')

    f.write('"/>')
    f.write('</svg>')
    f.close()

if __name__ == '__main__':
    if len(sys.argv) != 3:
        img_to_contour_svg()
    else:
        image_path = sys.argv[1]
        svg_path = sys.argv[2]
        img_to_contour_svg(image_path, svg_path)
