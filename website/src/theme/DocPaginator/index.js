import React from 'react';
import DocPaginator from '@theme-original/DocPaginator';
import NewsletterDoc from '@site/src/components/NewsletterDoc';
import TouchlabPro from '@site/src/components/TouchlabPro';
import Newsletter from '@site/src/components/Newsletter';

export default function DocPaginatorWrapper(props) {
  return (
    <>
        <DocPaginator {...props} />
        <div className="mt-8"><NewsletterDoc/></div>
    </>
  );
}
