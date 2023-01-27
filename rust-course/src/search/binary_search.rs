use std::cmp::Ordering;

pub fn binary_search<T: Ord>(arr: &[T], x: &T) -> Option<usize> {
    let len = arr.len();
    let mut l = 0;
    let mut r = len;
    while l < r {
        let m = l + (r - l) / 2;
        match x.cmp(&arr[m]) {
            Ordering::Less => r = m,
            Ordering::Equal => return Some(m),
            Ordering::Greater => l = m + 1,
        }
    }
    None
}

#[cfg(test)]
mod tests {
    use crate::search::binary_search::binary_search;

    #[test]
    fn emtpy() {
        let r = binary_search(&[], &"a");
        assert_eq!(r, None)
    }

    #[test]
    fn one_element() {
        let r = binary_search(&[1, 2], &1);
        assert_eq!(r, Some(0))
    }

    #[test]
    fn search_string() {
        let r = binary_search(&["a", "b", "c", "d", "google", "zoo"], &"a");
        assert_eq!(r, Some(0));
        let rr = binary_search(&["a", "b", "c", "d", "google", "zoo"], &"google");
        assert_eq!(rr, Some(4))
    }
}