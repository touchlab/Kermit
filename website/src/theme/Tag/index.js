import React from 'react';
import clsx from 'clsx';
import Link from '@docusaurus/Link';
import styles from './styles.module.css';
import BlogDocTag from "../../components/kmmpro/BlogDocTag";
export default function Tag({permalink, label, count}) {
  return (
      <BlogDocTag tag={{permalink:permalink, label:label, count:count}} showCount={false}/>
  );
}
