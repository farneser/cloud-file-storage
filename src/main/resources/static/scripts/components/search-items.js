Array.from(document.getElementsByClassName('post-link-file-last')).forEach(element => {
    const newSpan = document.createElement('span');
    newSpan.innerHTML = element.innerHTML;
    element.parentNode.replaceChild(newSpan, element);
});